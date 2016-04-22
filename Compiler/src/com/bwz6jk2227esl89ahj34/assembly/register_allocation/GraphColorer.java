package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister;

import java.util.*;

class MovePair {
    public final AssemblyAbstractRegister left;
    public final AssemblyAbstractRegister right;

    public MovePair(AssemblyAbstractRegister left, AssemblyAbstractRegister right) {
        this.left = left;
        this.right = right;
    }
}

public class GraphColorer {

    public static AssemblyPhysicalRegister[] colors = {
            AssemblyPhysicalRegister.RAX,
            AssemblyPhysicalRegister.RBX,
            AssemblyPhysicalRegister.RCX,
            AssemblyPhysicalRegister.R8
    };

    private static int K = colors.length;

    private Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph;
    private Stack<AssemblyAbstractRegister> removedNodes;
    private Set<AssemblyAbstractRegister> removed;
    private Set<AssemblyAbstractRegister> spillNodes;
    private Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring;

    private Set<MovePair> movePairs;
    private Set<MovePair> coalesced;

    public GraphColorer(Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph) {
        this.graph = graph;
        this.removedNodes = new Stack<>();
        this.removed = new HashSet<>();
        this.spillNodes = new HashSet<>();
        this.coloring = new HashMap<>();
        this.movePairs = new HashSet<>();
        this.coalesced = new HashSet<>();
    }

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

    public static AssemblyPhysicalRegister assignColor(Set<AssemblyPhysicalRegister> exclude) {
        // case where there is no available color
        AssemblyPhysicalRegister color = colors[(int) Math.floor(Math.random() * colors.length)];
        while (exclude.contains(color)) {
            color = colors[(int) Math.floor(Math.random() * colors.length)];
        }
        return color;
    }

    public void combineNodes(AssemblyAbstractRegister t1, AssemblyAbstractRegister t2) {
        if (!graph.containsKey(t1) || !graph.containsKey(t2)) {
            // one or both nodes do not exist in the graph
            return;
        }
        if (graph.get(t1).contains(t2) || graph.get(t2).contains(t1)) {
            // these two nodes are connected by an edge
            return;
        }

        for (AssemblyAbstractRegister node : graph.get(t2)) {
            if (!graph.get(t1).contains(node)) {
                graph.get(node).remove(t2);
                graph.get(node).add(t1);
                graph.get(t1).add(node);
            }
        }

        graph.remove(t2);
    }

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

    public boolean coalesce() {

        boolean coalescingOccurred = false;
        boolean changed;

        do {
            changed = false;
            for (MovePair pair : movePairs) {
                AssemblyAbstractRegister t1 = pair.left;
                AssemblyAbstractRegister t2 = pair.right;
                if (graph.get(t1).size() + graph.get(t2).size() < K) {
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

            if (graph.get(pair.left).size() < K) {
                frozen = pair.left;
            } else if (graph.get(pair.right).size() < K) {
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

            // reduced graph where all nodes are of significant degree
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
                if (numAdjacentColors(sn) < K) {
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

        return spillNodes.size() == 0;
    }

    public Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> getColoring() {
        return coloring;
    }

    public void addColoring(AssemblyAbstractRegister node, AssemblyPhysicalRegister color) {
        coloring.put(node, color);
    }
}
