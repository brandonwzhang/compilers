package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;

import java.util.*;

/**
 * A class for representing a pair of abstract registers that appear together
 * in a mov instrcution.
 */
class MovePair {
    final AssemblyAbstractRegister left;
    final AssemblyAbstractRegister right;

    MovePair(AssemblyAbstractRegister left, AssemblyAbstractRegister right) {
        this.left = left;
        this.right = right;
    }
}

/**
 * You must first perform live variable analysis on your program before using
 * this class. An instance of this class can used to allocate physical
 * registers to your program's abstract registers.
 */
public class GraphColorer {

    // the available colors for the graph
    public static AssemblyPhysicalRegister[] colors = {
            AssemblyPhysicalRegister.RAX,
            AssemblyPhysicalRegister.RBX,
            AssemblyPhysicalRegister.RCX,
            AssemblyPhysicalRegister.R8
    };

    // the interference graph
    private Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph;

    // nodes are added to this stack as we remove them
    private Stack<AssemblyAbstractRegister> removedNodes;

    // nodes that are removed are added here and retained in 'graph'
    private Set<AssemblyAbstractRegister> removed;

    // nodes that are potentially spill nodes
    // note that we must re-add nodes in the same order that we removed them,
    // but we should try to color potential spill nodes after all other nodes
    // are colored
    private Set<AssemblyAbstractRegister> spillNodes;

    // the set of node pairs that appear in a mov instruction together
    private Set<MovePair> movePairs;

    // the set of node pairs that were successfully coalesced
    private Set<MovePair> coalesced;

    // a mapping from abstract registers to physical registers
    // the "coloring" of our interference graph
    private Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring;

    private List<AssemblyLine> lines;

    // a constructor for testing purposes
    public GraphColorer(Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph, List<AssemblyLine> lines) {

        this.removedNodes = new Stack<>();
        this.removed = new HashSet<>();
        this.spillNodes = new HashSet<>();
        this.movePairs = new HashSet<>();
        this.coalesced = new HashSet<>();
        this.coloring = new HashMap<>();
        this.lines = lines;

        this.graph = graph;

        addMovePairs();
        removeImpossibleMovePairs();
    }

    /**
     * @param liveVariableSets the live variable sets from every program point in the program
     * @param lines the lines of assembly for which we are allocating registers
     */
    public GraphColorer(List<Set<AssemblyAbstractRegister>> liveVariableSets, List<AssemblyLine> lines) {

        this.removedNodes = new Stack<>();
        this.removed = new HashSet<>();
        this.spillNodes = new HashSet<>();
        this.movePairs = new HashSet<>();
        this.coalesced = new HashSet<>();
        this.coloring = new HashMap<>();
        this.lines = lines;

        this.graph = constructInterferenceGraph(liveVariableSets);

        addMovePairs();
        removeImpossibleMovePairs();
    }

    /**
     * Takes all of the live variable sets from the program.
     * Returns an interference graph.
     * Nodes are temps (abstract registers). Two nodes are connected by an edge
     * if their temps interfere with each other at any point in the program.
     */
    public static Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>>
        constructInterferenceGraph(List<Set<AssemblyAbstractRegister>> liveVariableSets) {

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();

        for (Set<AssemblyAbstractRegister> set : liveVariableSets) {

            List<AssemblyAbstractRegister> list = new ArrayList<>(set);

            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    AssemblyAbstractRegister t1 = list.get(i);
                    AssemblyAbstractRegister t2 = list.get(j);

                    // add nodes if they don't exist
                    if (!graph.keySet().contains(t1)) {
                        graph.put(t1, new ArrayList<>());
                    }
                    if (!graph.keySet().contains(t2)) {
                        graph.put(t2, new ArrayList<>());
                    }

                    // add edges if they don't exist
                    if (!graph.get(t1).contains(t2)) {
                        graph.get(t1).add(t2);
                    }
                    if (!graph.get(t2).contains(t1)) {
                        graph.get(t2).add(t1);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Finds all of the [mov t1 t2] pairs in the given assembly code.
     * Keeps track of them so move coalescing can be done later.
     */
    public void addMovePairs() {
        for (AssemblyLine line : lines) {
            if (!(line instanceof AssemblyInstruction)) {
                continue;
            }
            AssemblyInstruction instruction = (AssemblyInstruction) line;
            if (!(instruction.getOpCode() == OpCode.MOVQ)) {
                continue;
            }
            List<AssemblyExpression> args = instruction.args;

            boolean isMovePair = args.get(0) instanceof AssemblyAbstractRegister &&
                    args.get(1) instanceof AssemblyAbstractRegister;
            if (!isMovePair) {
                continue;
            }
            AssemblyAbstractRegister t1 = (AssemblyAbstractRegister) args.get(0);
            AssemblyAbstractRegister t2 = (AssemblyAbstractRegister) args.get(1);
            movePairs.add(new MovePair(t1, t2));
        }
    }

    /**
     * Call this after addMovePairs(). Removes any move pairs made up of temps
     * that interfere with each other.
     */
    public void removeImpossibleMovePairs() {
        Set<MovePair> removeSet = new HashSet<>();
        for (MovePair pair : movePairs) {
            if (graph.get(pair.left).contains(pair.right) || graph.get(pair.right).contains(pair.left)) {
                removeSet.add(pair);
            }
        }
        movePairs.removeAll(removeSet);
    }

    /**
     * Call this after graph coloring and move coalescing.
     * Removes any move instructions that were coasleced.
     * Mutates the given List of assembly lines.
     */
    public void updateLines() {
        int i = 0;
        while (i < lines.size()) {
            if (!(lines.get(i) instanceof AssemblyInstruction)) {
                i++;
                continue;
            }
            AssemblyInstruction instruction = (AssemblyInstruction) lines.get(i);
            if (!(instruction.getOpCode() == OpCode.MOVQ)) {
                i++;
                continue;
            }

            List<AssemblyExpression> args = instruction.args;
            boolean isMovePair = args.get(0) instanceof AssemblyAbstractRegister &&
                    args.get(1) instanceof AssemblyAbstractRegister;
            if (!isMovePair) {
                i++;
                continue;
            }

            AssemblyAbstractRegister t1 = (AssemblyAbstractRegister) args.get(0);
            AssemblyAbstractRegister t2 = (AssemblyAbstractRegister) args.get(1);
            for (MovePair pair : coalesced) {
                if (pair.left == t1 && pair.right == t2) {
                    lines.remove(i);
                    i--;
                    break;
                }
            }
            i++;
        }
    }

    /**
     * A method for selecting a random valid color.
     * @param exclude registers that should be excluded from the selection
     * @return a random valid register
     */
    public static AssemblyPhysicalRegister assignColor(Set<AssemblyPhysicalRegister> exclude) {
        // case where there is no available color
        AssemblyPhysicalRegister color = colors[(int) Math.floor(Math.random() * colors.length)];
        while (exclude.contains(color)) {
            color = colors[(int) Math.floor(Math.random() * colors.length)];
        }
        return color;
    }

    public void combineNodes(AssemblyAbstractRegister t1, AssemblyAbstractRegister t2) {

        for (AssemblyAbstractRegister node : graph.get(t2)) {
            if (!graph.get(t1).contains(node)) {
                graph.get(node).add(t1);
                graph.get(t1).add(node);
            }
        }

        for (AssemblyAbstractRegister node : graph.keySet()) {
            graph.get(node).remove(t2);
        }

        graph.remove(t2);
    }

    /**
     * A node is move-related if it is part of a mov instruction that has a
     * chance of being coalesced. i.e., it came from a mov with two temps,
     * and those temps do not interfere.
     *
     * @param node a node in the interference graph
     * @return true if the given node is move-related
     */
    public boolean isMoveRelated(AssemblyAbstractRegister node) {
        for (MovePair pair : movePairs) {
            if (pair.left == node || pair.right == node) {
                return true;
            }
        }

        return false;
    }

    public Optional<AssemblyAbstractRegister> getRemovableNode() {
        for (AssemblyAbstractRegister node : graph.keySet()) {
            if (graph.get(node).size() < colors.length && !removed.contains(node) && !coloring.containsKey(node) && !isMoveRelated(node)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    /**
     * @param n a node in the interference graph
     * @return the number of adjacent nodes that are already colored
     */
    public int numAdjacentColors(AssemblyAbstractRegister n) {
        int result = 0;
        for (AssemblyAbstractRegister neighbor : graph.get(n)) {
            if (coloring.containsKey(neighbor)) {
                result++;
            }
        }
        return result;
    }

    public boolean simplify() {

        boolean changed = false;

        while (true) {
            Optional<AssemblyAbstractRegister> removable = getRemovableNode();
            if (!removable.isPresent()) {
                break;
            }

            AssemblyAbstractRegister removedNode = removable.get();

            removedNodes.push(removedNode);
            removed.add(removedNode);

            for (AssemblyAbstractRegister neighbor : graph.get(removedNode)) {
                graph.get(neighbor).remove(removedNode);
            }

            changed = true;
        }

        return changed;
    }

    public int coalescedDegree(AssemblyAbstractRegister t1, AssemblyAbstractRegister t2) {
        int result = graph.get(t1).size() + graph.get(t2).size();

        for (AssemblyAbstractRegister n : graph.get(t1)) {
            if (graph.get(t2).contains(n)) {
                result--;
            }
        }

        return result;
    }

    public boolean coalesce() {

        boolean coalescingOccurred = false;
        boolean changed;

        do {
            changed = false;
            for (MovePair pair : movePairs) {
                AssemblyAbstractRegister t1 = pair.left;
                AssemblyAbstractRegister t2 = pair.right;
                if (coalescedDegree(t1, t2) <= colors.length) {
                    coalesced.add(pair);
                    combineNodes(t1, t2);
                    movePairs.remove(pair);
                    Set<MovePair> removeSet = new HashSet<>();
                    Set<MovePair> addSet = new HashSet<>();
                    for (MovePair pair_ : movePairs) {
                        if (pair_.left == t2) {
                            removeSet.add(pair_);
                            addSet.add(new MovePair(t1, pair_.right));
                        } else if(pair_.right == t2) {
                            removeSet.add(pair_);
                            addSet.add(new MovePair(pair_.left, t1));
                        }
                    }
                    movePairs.removeAll(removeSet);
                    movePairs.addAll(addSet);
                    coalescingOccurred = true;
                    changed = true;
                    break;
                }
            }
        } while (changed);

        return coalescingOccurred;
    }

    public boolean freeze() {

        boolean frozeNode = false;

        AssemblyAbstractRegister frozen = null;
        for (MovePair pair : movePairs) {

            if (graph.get(pair.left).size() < colors.length) {
                frozen = pair.left;
            } else if (graph.get(pair.right).size() < colors.length) {
                frozen = pair.right;
            }

            if (frozen != null) {
                Set<MovePair> removeSet = new HashSet<>();
                for (MovePair pair_ : movePairs) {
                    if (graph.get(pair_.left) == frozen || graph.get(pair_.right) == frozen) {
                        removeSet.add(pair_);
                    }
                }
                movePairs.removeAll(removeSet);
                frozeNode = true;
                break;
            }
        }

        return frozeNode;
    }

    /**
     * Repeat simplify and coalesce until no changes occur.
     * Freeze a move-related node and continue simplfiying and coalescing.
     * If no nodes can be frozen, then remove a node of significant degree and
     * mark it as a potential spill node. Continue simplify-coalesce-freeze.
     * Once the graph is empty, begin adding nodes back and coloring as we go.
     * Potential spill nodes are added at the appropriate time, but are colored
     * last. If they cannot be colored, then they become actual spill nodes,
     * and the graph coloring fails.
     * @return True if the graph was successfully colored. False otherwise.
     */
    public boolean colorGraph() {

        while (true) {
            boolean frozeNode;
            do {
                boolean changed;
                do {
                    System.out.println("simplify");
                    changed = simplify();
                    System.out.println("coalesce");
                    changed = coalesce() || changed;
                } while (changed);
                System.out.println("freeze");
                frozeNode = freeze();
            } while(frozeNode);

            if (graph.size() == removed.size()) {
                break;
            }

            // potential spill node
            System.out.println("potential spill");
            AssemblyAbstractRegister spillNode = graph.keySet().iterator().next();
            for (AssemblyAbstractRegister neighbor : graph.get(spillNode)) {
                graph.get(neighbor).remove(spillNode);
            }
            removedNodes.push(spillNode);
            removed.add(spillNode);
            spillNodes.add(spillNode);
            Set<MovePair> removeSet = new HashSet<>();
            for (MovePair pair : movePairs) {
                if (pair.left == spillNode || pair.right == spillNode) {
                    removeSet.add(pair);
                }
            }
            movePairs.removeAll(removeSet);
        }

        System.out.println("begin coloring");
        AssemblyAbstractRegister currentNode;
        while (!removedNodes.isEmpty()) {
            currentNode = removedNodes.pop();
            removed.remove(currentNode);
            Set<AssemblyPhysicalRegister> neighborColors = new HashSet<>();
            // readd currentNode to the adjacency lists of its neighbors
            for (AssemblyAbstractRegister neighbor : graph.get(currentNode)) {
                graph.get(neighbor).add(currentNode);
                if (coloring.containsKey(neighbor)) {
                    neighborColors.add(coloring.get(neighbor));
                }
            }

            if (!spillNodes.contains(currentNode)) {
                if (!coloring.containsKey(currentNode)) {
                    AssemblyPhysicalRegister color = assignColor(neighborColors);
                    coloring.put(currentNode, color);
                }
            }
        }

        System.out.println("color the spill nodes");
        while (spillNodes.size() > 0) {
            AssemblyAbstractRegister colorable = null;
            for (AssemblyAbstractRegister sn : spillNodes) {
                if (numAdjacentColors(sn) < colors.length) {
                    colorable = sn;
                    break;
                }
            }
            if (colorable == null) {
                break;
            }

            Set<AssemblyPhysicalRegister> neighborColors = new HashSet<>();
            for (AssemblyAbstractRegister neighbor : graph.get(colorable)) {
                if (coloring.containsKey(neighbor)) {
                    neighborColors.add(coloring.get(neighbor));
                }
            }
            AssemblyPhysicalRegister color = assignColor(neighborColors);
            coloring.put(colorable, color);
            spillNodes.remove(colorable);
        }

        updateLines();

        return spillNodes.size() == 0;
    }

    public Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> getColoring() {
        return coloring;
    }

    public void addColoring(AssemblyAbstractRegister node, AssemblyPhysicalRegister color) {
        coloring.put(node, color);
    }
}
