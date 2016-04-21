package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;

import java.util.*;

public class GraphColorer {
//    private static Set<Register> colors = new HashSet<>();
//
//    static {
//        colors.add(Register.RAX);
//        colors.add(Register.RBX);
//        colors.add(Register.RCX);
//    }

    private static Register[] colors = {Register.RAX, Register.RBX};
    private static Stack<Node> removedNodes = new Stack<>();
    private static Set<Node> spillNodes = new HashSet<>();
    private List<Node> graph;

    public GraphColorer(List<Node> graph) {
        // shallow copy of graph
        this.graph = new ArrayList<>();
        for (Node n : graph) {
            this.graph.add(n);
        }
        Collections.shuffle(this.graph);
    }

    public static Register assignColor(Set<Register> exclude) {
        // case where there is no available color
        Register color = colors[(int) Math.floor(Math.random() * colors.length)];
        while (exclude.contains(color)) {
            color = colors[(int) Math.floor(Math.random() * colors.length)];
        }
        return color;
    }

    public static Optional<Node> getRemovableNode(List<Node> graph) {
        for (Node n : graph) {
            if (n.neighbors.size() < colors.length) {
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }

    public static boolean hasUncoloredNode(List<Node> graph) {
        for (Node n : graph) {
            if (n.color == null) {
                return true;
            }
        }
        return false;
    }

    public static int numAdjacentColors(Node n) {
        int result = 0;
        for (Node neighbor : n.neighbors) {
            if (neighbor.color != null) {
                result++;
            }
        }

        return result;
    }

    public boolean colorGraph() {
        if (graph.size() == 0) {
            return true;
        }

        int k = colors.length;

        System.out.println("remove nodes");
        while (hasUncoloredNode(graph)) {
            Node removedNode;

            Optional<Node> removableNode = getRemovableNode(graph);
            if (removableNode.isPresent()) {
                removedNode = removableNode.get();
            } else {
                // graph cannot be simplified
                // remove a node that will potentially spill
                removedNode = graph.get(0);
                removedNode.potentialSpill = true;
            }

            // push onto stack
            removedNodes.push(removedNode);

            // remove removedNode from the adjacency lists of its neighbors
            for (Node n : removedNode.neighbors) {
                n.removeNeighbor(removedNode);
            }

            graph.remove(removedNode);
        }

        System.out.println("color nodes");
        Node currentNode;
        while (!removedNodes.isEmpty()) {
            currentNode = removedNodes.pop();
            Set<Register> neighborColors = new HashSet<>();
            // readd currentNode to the adjacency lists of its neighbors
            for (Node neighbor : currentNode.neighbors) {
                neighbor.addNeighbor(currentNode);
                if (neighbor.color != null) {
                    neighborColors.add(neighbor.color);
                }
            }

            if (currentNode.potentialSpill) {
                spillNodes.add(currentNode);
            } else if (currentNode.color == null) {
                currentNode.color = assignColor(neighborColors);
            }

        }

        System.out.println("color spill nodes");
        while (spillNodes.size() > 0) {
            Node colorable = null;
            for (Node sn : spillNodes) {
                if (numAdjacentColors(sn) < k) {
                    colorable = sn;
                    break;
                }
            }
            if (colorable == null) {
                break;
            }

            Set<Register> neighborColors = new HashSet<>();
            for (Node neighbor : colorable.neighbors) {
                if (neighbor.color != null) {
                    neighborColors.add(neighbor.color);
                }
            }
            colorable.color = assignColor(neighborColors);
            spillNodes.remove(colorable);
        }

        return spillNodes.size() == 0;
    }
}
