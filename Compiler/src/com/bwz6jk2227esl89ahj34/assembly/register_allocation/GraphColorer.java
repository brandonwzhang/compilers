package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.optimization.OptimizationType;

import java.util.*;

/**
 * You must first perform live variable analysis on your program before using
 * this class. An instance of this class can used to allocate physical
 * registers to your program's abstract registers.
 */
public class GraphColorer {

    /**
     * A class for representing a pair of abstract registers that appear together
     * in a mov instruction. Instances are immutable.
     */
    class MovePair {
        final AssemblyAbstractRegister left;
        final AssemblyAbstractRegister right;

        MovePair(AssemblyAbstractRegister left, AssemblyAbstractRegister right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "[" + this.left.toString() + ", " + this.right.toString() + "]";
        }

        // See the comment for equals()
        @Override
        public int hashCode() {
            return this.left.hashCode() + this.right.hashCode();
        }

        // This ensures that (a, b) is equivalent to the pair (b, a)
        @Override
        public boolean equals(Object o) {
            MovePair other = (MovePair) o;
            boolean result = this.left.equals(other.left) && this.right.equals(other.right);
            result = (this.left.equals(other.right) && this.right.equals(other.left)) || result;
            return result;
        }
    }

    // The available colors for the graph
    public static AssemblyPhysicalRegister[] colors = AssemblyPhysicalRegister.availableRegisters;

    // A mapping from abstract registers to physical registers
    // The "coloring" of our interference graph
    private Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring;

    // The interference graph
    private Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph;

    // Nodes are added to this stack as we *temporarily* remove them
    private Deque<AssemblyAbstractRegister> removedNodes;

    // Nodes that have not been removed yet are members of this set
    private Set<AssemblyAbstractRegister> activeNodes;

    /*
        While the algorithm runs, potential spill nodes are stored here.
        Note that we must re-add nodes in the same order that we removed them,
        but we should try to color potential spill nodes after all other nodes
        are colored. After the algorithm finishes, the nodes remaining in this
        set are the actual spill nodes.
     */
    private Set<AssemblyAbstractRegister> spillNodes;

    // the set of node pairs that appear in a mov instruction together
    private Set<MovePair> movePairs;

    /*
        Maps nodes that were coalesced and deleted to the node that stayed.
        Note that the node that stayed may have been deleted later on, which would make
        it a key in this map too. Thus, deleted nodes form a chain of at least length
        two where the last node in the chain is the final replacement.
     */
    private Map<AssemblyAbstractRegister, AssemblyAbstractRegister> replacementMap;

    // The lines of assembly code that we are allocating registers for
    private List<AssemblyLine> lines;

    /**
     * @param liveVariableSets the live variable sets from every program point in the program
     * @param lines the lines of assembly for which we are allocating registers
     */
    public GraphColorer(List<Set<AssemblyAbstractRegister>> liveVariableSets, List<AssemblyLine> lines) {

        this.coloring = new LinkedHashMap<>();
        this.removedNodes = new ArrayDeque<>();
        this.activeNodes = new LinkedHashSet<>();
        this.spillNodes = new LinkedHashSet<>();
        this.movePairs = new LinkedHashSet<>();
        this.replacementMap = new LinkedHashMap<>();
        this.lines = lines;

        this.graph = constructInterferenceGraph(liveVariableSets);
        this.activeNodes.addAll(graph.keySet());

        addMovePairs();
        removeAbsentMovePairs();
        removeImpossibleMovePairs();

        if (!Main.optimizationOn(OptimizationType.MC)) {
            movePairs.clear();
        } else {
            if (Main.debugOn()) {
                //System.out.println("DEBUG: performing optimization: move coalescing");
            }
        }
    }

    /**
     * Use this instructor to include precolored nodes.
     *
     * This probably does not work right now.
     */
    public GraphColorer(
            List<Set<AssemblyAbstractRegister>> liveVariableSets,
            List<AssemblyLine> lines,
            Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> precoloring
    ) {
        this.coloring = new LinkedHashMap<>();
        this.coloring.putAll(precoloring);
        this.removedNodes = new ArrayDeque<>();
        this.activeNodes = new LinkedHashSet<>();
        this.spillNodes = new LinkedHashSet<>();
        this.movePairs = new LinkedHashSet<>();
        this.replacementMap = new LinkedHashMap<>();
        this.lines = lines;

        this.graph = constructInterferenceGraph(liveVariableSets);
        this.activeNodes.addAll(graph.keySet());

        addMovePairs();
        removeAbsentMovePairs();
        removeImpossibleMovePairs();

        if (!Main.optimizationOn(OptimizationType.MC)) {
            movePairs.clear();
        } else {
            if (Main.debugOn()) {
                //System.out.println("DEBUG: performing optimization: move coalescing");
            }
        }

        //System.out.println(precoloring);
    }

    /**
     * @return a mapping from abstract registers to physical registers
     */
    public Map<AssemblyAbstractRegister, AssemblyExpression> getColoring() {
        Map<AssemblyAbstractRegister, AssemblyExpression> coloring_ = new LinkedHashMap<>();
        coloring_.putAll(coloring);
        return coloring_;
    }

    /**
     * Takes all of the live variable sets from the program.
     * Returns an interference graph.
     * Nodes are temps (abstract registers). Two nodes are connected by an edge
     * if their temps interfere with each other at any point in the program.
     */
    private static Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>>
        constructInterferenceGraph(List<Set<AssemblyAbstractRegister>> liveVariableSets) {

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new LinkedHashMap<>();

        for (Set<AssemblyAbstractRegister> set : liveVariableSets) {

            List<AssemblyAbstractRegister> list = new ArrayList<>(set);

            for (AssemblyAbstractRegister node : set) {
                if (!graph.containsKey(node)) {
                    graph.put(node, new ArrayList<>());
                }
            }

            for (int i = 0; i < list.size() - 1; i++) {
                AssemblyAbstractRegister a = list.get(i);

                for (int j = i + 1; j < list.size(); j++) {

                    AssemblyAbstractRegister b = list.get(j);

                    // add edges if they don't exist
                    // graph.get(a).contains(b) iff graph.get(b).contains(a)
                    // because of how we construct the adjacency lists
                    if (!graph.get(a).contains(b)) {
                        graph.get(a).add(b);
                        graph.get(b).add(a);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Finds all of the [mov t1 t2] pairs in the given assembly code.
     * Keeps track of them so move coalescing can be done later.
     * Does not add move pairs where t1 and t2 the same.
     */
    private void addMovePairs() {
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

            AssemblyAbstractRegister a = (AssemblyAbstractRegister) args.get(0);
            AssemblyAbstractRegister b = (AssemblyAbstractRegister) args.get(1);

            if (a.equals(b)) {
                continue;
            }

            movePairs.add(new MovePair(a, b));
        }
    }

    /**
     * Call this after addMovePairs. Removes any move pairs that contain nodes
     * that are not in the graph.
     */
    private void removeAbsentMovePairs() {
        Set<MovePair> removeSet = new LinkedHashSet<>();
        for (MovePair pair : movePairs) {
            if (graph.get(pair.left) == null || graph.get(pair.right) == null) {
                removeSet.add(pair);
            }
        }
        movePairs.removeAll(removeSet);
    }

    /**
     * Call this after removeAbsentMovePairs(). Removes any move pairs made up of temps
     * that interfere with each other.
     */
    private void removeImpossibleMovePairs() {
        Set<MovePair> removeSet = new LinkedHashSet<>();
        for (MovePair pair : movePairs) {
            if (graph.get(pair.left).contains(pair.right) || graph.get(pair.right).contains(pair.left)) {
                removeSet.add(pair);
            }
            if (coloring.containsKey(pair.left) || coloring.containsKey(pair.right)) {
                //System.out.println(pair);
                removeSet.add(pair);
            }
        }
        movePairs.removeAll(removeSet);
    }

    private void allocateCoalescedRegisters() {
        for (AssemblyAbstractRegister register : replacementMap.keySet()) {

            AssemblyAbstractRegister replacement = register;
            while (replacementMap.containsKey(replacement)) {
                replacement = replacementMap.get(replacement);
            }

            coloring.put(register, coloring.get(replacement));
        }
    }

    private AssemblyAbstractRegister getReplacement(AssemblyAbstractRegister register) {
        while (replacementMap.keySet().contains(register)) {
            register = replacementMap.get(register);
        }
        return register;
    }

    /**
     * Call this after graph coloring and move coalescing.
     * Removes any move instructions that were coalesced.
     * Mutates the given List of assembly lines.
     */
    private void updateLines() {

        for (AssemblyLine line : lines) {
            if (!(line instanceof AssemblyInstruction)) {
                continue;
            }
            AssemblyInstruction instruction = (AssemblyInstruction) line;
            List<AssemblyExpression> args = instruction.args;

            for (int i = 0; i < args.size(); i++) {
                AssemblyAbstractRegister register;

                if (args.get(i) instanceof AssemblyAbstractRegister) {
                    register = (AssemblyAbstractRegister) args.get(i);
                    args.set(i, getReplacement(register));

                } else if (args.get(i) instanceof AssemblyMemoryLocation) {
                    AssemblyMemoryLocation mem = (AssemblyMemoryLocation) args.get(i);
                    if (mem.offsetRegister instanceof AssemblyAbstractRegister) {
                        register = (AssemblyAbstractRegister) mem.offsetRegister;
                        mem.setOffsetRegister(getReplacement(register));

                    } else if (mem.baseRegister instanceof AssemblyAbstractRegister) {
                        register = (AssemblyAbstractRegister) mem.baseRegister;
                        mem.setBaseRegister(getReplacement(register));

                    }
                }
            }
        }

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
            if (args.get(0).equals(args.get(1))){
                lines.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * A method for selecting a random valid color. Returns null if all of
     * the valid registers are excluded.
     *
     * @param exclude registers that should be excluded from the selection
     * @return a random valid register
     */
    public static AssemblyPhysicalRegister assignColor(Set<AssemblyPhysicalRegister> exclude) {

//        if (exclude.containsAll(Arrays.asList(colors))) {
//            return null;
//        }
//
//        AssemblyPhysicalRegister color = colors[(int) Math.floor(Math.random() * colors.length)];
//        while (exclude.contains(color)) {
//            color = colors[(int) Math.floor(Math.random() * colors.length)];
//        }

        // Deterministic coloring
        AssemblyPhysicalRegister color = colors[0];
        int i = 0;
        while (exclude.contains(color)) {
            if (i < colors.length) {
                color = colors[i];
                i++;
            } else {
                color = null;
                break;
            }
        }

        return color;
    }

    /**
     * Merges two nodes in the interference graph. Should not be called
     * if an edge exists between the two nodes.
     *
     * t1 gains edges to all of t2's neighbors (if t1 already has an edge to
     * a neighbor of t2, a second edge is not added). t2 and all edges
     * connected to it are removed.
     *
     * Requires: Both nodes must be in the graph. Both nodes are not connected
     * by an edge
     */
    private void combineNodes(AssemblyAbstractRegister a, AssemblyAbstractRegister b) {

        assert activeNodes.contains(a) && activeNodes.contains(b);
        assert !graph.get(a).contains(b) && !graph.get(b).contains(a);
        assert !coloring.containsKey(a) && !coloring.containsKey(b);

        for (AssemblyAbstractRegister node : graph.get(b)) {

            if (!graph.get(a).contains(node)) {
                graph.get(a).add(node);
            }
            if (!graph.get(node).contains(a)) {
                graph.get(node).add(a);
            }
            graph.get(node).remove(b);
        }

        for (AssemblyAbstractRegister node : removedNodes) {
            if (graph.get(node).remove(b)) {
                if (!graph.get(node).contains(a)) {
                    graph.get(node).add(a);
                }
            }
        }

        graph.remove(b);
        activeNodes.remove(b);
    }

    /**
     * A node is move-related if it is part of a mov instruction that has a
     * chance of being coalesced. i.e., it came from a mov with two temps,
     * and those temps do not interfere.
     *
     * @param node a node in the interference graph
     * @return true if the given node is move-related
     */
    private boolean isMoveRelated(AssemblyAbstractRegister node) {

        for (MovePair pair : movePairs) {
            if (pair.left.equals(node) || pair.right.equals(node)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a node in that can be removed at the simplify stage.
     *
     * A node is removable if:
     *      (1) it has less neighbors than the number of colors
     *      (2) it is not a pre-colored node
     *      (3) it is not a move-related node
     */
    private Optional<AssemblyAbstractRegister> getRemovableNode() {

        for (AssemblyAbstractRegister node : activeNodes) {
            if (
                        graph.get(node).size() < colors.length // insignificant
                    && !isMoveRelated(node)                    // is not move-related
                    ) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    /**
     * @param n a node in the interference graph
     * @return the number of adjacent nodes that are already colored
     */
    private int numAdjacentColors(AssemblyAbstractRegister n) {

        Set<AssemblyPhysicalRegister> neighborColors = new LinkedHashSet<>();
        for (AssemblyAbstractRegister neighbor : graph.get(n)) {
            if (coloring.containsKey(neighbor)) {
                neighborColors.add(coloring.get(neighbor));
            }
        }

        return neighborColors.size();
    }

    /**
     * Simplifying a graph means removing nodes of insignificant degree.
     * A call to simplify will remove nodes until all of the nodes left
     * are move-related or have significant degree.
     *
     * Let K be the number of available colors.
     * Significant: K or more neighbors
     * Insignificant: less than K neighbors
     *
     * @return True if any nodes were removed. False otherwise.
     */
    private boolean simplify() {

        boolean changed = false;

        while (true) {
            Optional<AssemblyAbstractRegister> removable = getRemovableNode();
            if (!removable.isPresent()) {
                break;  // cannot simplify further
            }

            AssemblyAbstractRegister removedNode = removable.get();

            removedNodes.push(removedNode);
            activeNodes.remove(removedNode);
            //System.out.println("--" + "removing " + removedNode);

            for (AssemblyAbstractRegister neighbor : graph.get(removedNode)) {
                // remove removedNode from the adjacency lists of its neighbors
                graph.get(neighbor).remove(removedNode);
            }

            changed = true;
        }

        return changed;
    }

    /**
     * Returns true if the two given nodes can be coalesced.
     *
     * Requires: a and b do not have an edge between them
     */
    private boolean canCoalesce(AssemblyAbstractRegister a, AssemblyAbstractRegister b) {

        if (graph.get(a).contains(b) || graph.get(b).contains(a)) {
            return false;
        }

        if (coloring.containsKey(a) || coloring.containsKey(b)) {
            // don't coalesce precolored nodes
            return false;
        }

        // George's
//        List<AssemblyAbstractRegister> a_neighbors = graph.get(a);
//        List<AssemblyAbstractRegister> b_neighbors = graph.get(b);
//        assert !a_neighbors.contains(b);
//        for (AssemblyAbstractRegister neighbor : a_neighbors) {
//            // George's conservative coalescing
//            if (!b_neighbors.contains(neighbor) && graph.get(neighbor).size() >= colors.length) {
//                return false;
//            }
//        }
//
//        return true;

        // Brigg's
        Set<AssemblyAbstractRegister> ab_neighbors = new LinkedHashSet<>();
        ab_neighbors.addAll(graph.get(a));
        ab_neighbors.addAll(graph.get(b));
        int significantNeighbors = 0;
        for (AssemblyAbstractRegister neighbor : ab_neighbors) {
            if (graph.get(neighbor).size() >= colors.length) {
                significantNeighbors++;
            }
        }

        return significantNeighbors < colors.length;
    }

    /**
     * @return True if at least one pair of nodes was coalesced.
     *         False otherwise.
     */
    private boolean coalesce() {
        // TODO: coalescing a precolored node
        boolean coalescingOccurred = false;
        boolean changed;

        do {
            changed = false;
            Set<MovePair> addSet = new LinkedHashSet<>();
            Set<MovePair> removeSet = new LinkedHashSet<>();
            for (MovePair pair : movePairs) {
                AssemblyAbstractRegister t1 = pair.left;
                AssemblyAbstractRegister t2 = pair.right;
                if (canCoalesce(t1, t2)) {
                    //System.out.println("--coalescing: " + t1 + " and " + t2);
                    combineNodes(t1, t2);
                    replacementMap.put(t2, t1);
                    removeSet.add(pair);
                    for (MovePair originalPair : movePairs) {

                        if (originalPair.left.equals(t2)) {
                            addSet.add(new MovePair(t1, originalPair.right));
                            removeSet.add(originalPair);

                        } else if(originalPair.right.equals(t2)) {
                            addSet.add(new MovePair(originalPair.left, t1));
                            removeSet.add(originalPair);
                        }
                    }
                    // The previous code added a (t1, t1) pair that needs
                    // to be removed.
                    removeSet.add(new MovePair(t1, t1));
                    coalescingOccurred = true;
                    changed = true;
                    break;
                }
            }
            movePairs.addAll(addSet);       // must be called in this order
            movePairs.removeAll(removeSet);
        } while (changed);

        return coalescingOccurred;
    }

    private boolean freeze() {

        boolean frozeNode = false;

        AssemblyAbstractRegister frozen = null;
        for (MovePair pair : movePairs) {

            // pick a move-related node of insignificant degree to freeze
            if (graph.get(pair.left).size() < colors.length) {
                frozen = pair.left;
            } else if (graph.get(pair.right).size() < colors.length) {
                frozen = pair.right;
            }

            // give up on all move pairs that contain the node we are freezing
            if (frozen != null) {
                //System.out.println("--freezing " + frozen);
                Set<MovePair> removeSet = new LinkedHashSet<>();
                for (MovePair pair_ : movePairs) {
                    if (pair_.left.equals(frozen) || pair_.right.equals(frozen)) {
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
     *
     * Freeze a move-related node and continue simplfiying and coalescing.
     *
     * If no nodes can be frozen, then remove a node of significant degree and
     * mark it as a potential spill node. Continue simplify-coalesce-freeze.
     *
     * Once the graph is empty, begin adding nodes back and coloring as we go.
     * Potential spill nodes are added back at the appropriate time, but they
     * are colored last. If they cannot be colored, then they become actual
     * spill nodes, and the graph coloring fails.
     *
     * @return True if the graph was successfully colored. False otherwise.
     */
    public void colorGraph() {

        //System.out.println("\n========= colorGraph() called =========");

        for (AssemblyAbstractRegister precoloredNode : coloring.keySet()) {
            activeNodes.remove(precoloredNode);
        }

        while (true) {
            boolean frozeNode;
            do {
                boolean changed;
                do {
                    //System.out.println("simplify");
                    changed = simplify();
                    //System.out.println("coalesce");
                    changed = coalesce() || changed;
                } while (changed);
                //System.out.println("freeze");
                frozeNode = freeze();
            } while(frozeNode);

            if (activeNodes.size() == 0) {
                // TODO: test precolored nodes
                break;
            }

            // potential spill node
            Iterator<AssemblyAbstractRegister> iterator = activeNodes.iterator();
            AssemblyAbstractRegister spillNode = iterator.next();

            //System.out.println("potential spill " + spillNode);
            for (AssemblyAbstractRegister neighbor : graph.get(spillNode)) {
                graph.get(neighbor).remove(spillNode);
            }
            removedNodes.push(spillNode);
            iterator.remove();
            spillNodes.add(spillNode);
            Set<MovePair> removeSet = new LinkedHashSet<>();
            for (MovePair pair : movePairs) {
                if (pair.left.equals(spillNode) || pair.right.equals(spillNode)) {
                    removeSet.add(pair);
                }
            }
            movePairs.removeAll(removeSet);
        }

        //System.out.println("begin coloring");
        AssemblyAbstractRegister currentNode;
        while (!removedNodes.isEmpty()) {
            currentNode = removedNodes.pop();
            activeNodes.add(currentNode);
            Set<AssemblyPhysicalRegister> neighborColors = new LinkedHashSet<>();
            // read currentNode to the adjacency lists of its neighbors
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
                    //System.out.println("--assigned " + color + " to " + currentNode);
                }
            }
        }

        //System.out.println("color the spill nodes");
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

            Set<AssemblyPhysicalRegister> neighborColors = new LinkedHashSet<>();
            for (AssemblyAbstractRegister neighbor : graph.get(colorable)) {
                if (coloring.containsKey(neighbor)) {
                    neighborColors.add(coloring.get(neighbor));
                }
            }
            AssemblyPhysicalRegister color = assignColor(neighborColors);
            coloring.put(colorable, color);
            spillNodes.remove(colorable);
            //System.out.println("colored potential spill node " + colorable);
        }

        //System.out.println("spill nodes: " + spillNodes.size());
        allocateCoalescedRegisters();
        //updateLines();
    }

    /**
     * @return True if the entire graph was successfully colored. False otherwise.
     */
    public boolean colored() {
        return spillNodes.size() == 0;
    }

    // a constructor for testing purposes
    public GraphColorer(Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph, List<AssemblyLine> lines) {
        this(new ArrayList<>(), lines);
        this.graph = graph;
        this.activeNodes.clear();
        this.activeNodes.addAll(graph.keySet());
    }
}
