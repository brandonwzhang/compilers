package com.bwz6jk2227esl89ahj34.assembly.register_allocation;


import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister.Register;

import java.util.*;

public class GraphColorer2 {

    public static Register[] colors = {Register.RAX, Register.RBX, Register.RCX, Register.R8};

    private Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph;
    private Stack<AssemblyAbstractRegister> removedNodes;
    private Set<AssemblyAbstractRegister> removed;
    private Set<AssemblyAbstractRegister> spillNodes;
    private Map<AssemblyAbstractRegister, Register> coloring;

    public GraphColorer2(Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph) {
        this.graph = graph;
        this.removedNodes = new Stack<>();
        this.removed = new HashSet<>();
        this.spillNodes = new HashSet<>();
        this.coloring = new HashMap<>();
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

    public static Register assignColor(Set<Register> exclude) {
        // case where there is no available color
        Register color = colors[(int) Math.floor(Math.random() * colors.length)];
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

    public void addColoring(AssemblyAbstractRegister node, Register color) {
        coloring.put(node, color);
    }
    public Map<AssemblyAbstractRegister, Register> getColoring() {
        return coloring;
    }

    public Optional<AssemblyAbstractRegister> getRemovableNode() {
        for (AssemblyAbstractRegister node : graph.keySet()) {
            if (graph.get(node).size() < colors.length && !removed.contains(node) && !coloring.containsKey(node)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public boolean hasUncoloredNode() {
        for (AssemblyAbstractRegister node : graph.keySet()) {
            if (!coloring.containsKey(node) && !removed.contains(node)) {
                return true;
            }
        }
        return false;
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

    public AssemblyAbstractRegister getRandomNode() {
        return graph.keySet().iterator().next();
    }

    public boolean colorGraph() {
        if (graph.size() == 0) {
            return true;
        }

        int k = colors.length;

        System.out.println("remove nodes");
        while (hasUncoloredNode()) {
            AssemblyAbstractRegister removedNode;

            Optional<AssemblyAbstractRegister> removableNode = getRemovableNode();
            if (removableNode.isPresent()) {
                removedNode = removableNode.get();
            } else {
                // graph cannot be simplified
                // remove a node that will potentially spill
                removedNode = graph.keySet().iterator().next();
                spillNodes.add(removedNode);
            }

            // push onto stack
            removedNodes.push(removedNode);
            removed.add(removedNode);

            // remove removedNode from the adjacency lists of its neighbors
            for (AssemblyAbstractRegister neighbor : graph.get(removedNode)) {
                graph.get(neighbor).remove(removedNode);
            }
        }

        System.out.println("color the nodes");
        AssemblyAbstractRegister currentNode;
        while (!removedNodes.isEmpty()) {
            currentNode = removedNodes.pop();
            removed.remove(currentNode);
            Set<Register> neighborColors = new HashSet<>();
            // readd currentNode to the adjacency lists of its neighbors
            for (AssemblyAbstractRegister neighbor : graph.get(currentNode)) {
                graph.get(neighbor).add(currentNode);
                if (coloring.containsKey(neighbor)) {
                    neighborColors.add(coloring.get(neighbor));
                }
            }

            if (!spillNodes.contains(currentNode)) {
                if (!coloring.containsKey(currentNode)) {
                    Register color = assignColor(neighborColors);
                    coloring.put(currentNode, color);
                }
            }
        }

        System.out.println("color the spill nodes");
        while (spillNodes.size() > 0) {
            AssemblyAbstractRegister colorable = null;
            for (AssemblyAbstractRegister sn : spillNodes) {
                if (numAdjacentColors(sn) < k) {
                    colorable = sn;
                    break;
                }
            }
            if (colorable == null) {
                break;
            }

            Set<Register> neighborColors = new HashSet<>();
            for (AssemblyAbstractRegister neighbor : graph.get(colorable)) {
                if (coloring.containsKey(neighbor)) {
                    neighborColors.add(coloring.get(neighbor));
                }
            }
            Register color = assignColor(neighborColors);
            coloring.put(colorable, color);
            spillNodes.remove(colorable);
        }

        return spillNodes.size() == 0;
    }
}
