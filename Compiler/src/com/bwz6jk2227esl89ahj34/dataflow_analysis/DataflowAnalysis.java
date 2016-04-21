package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import java.util.*;

public abstract class DataflowAnalysis {
    public enum Direction {
        FORWARD, BACKWARD
    }

    private Direction direction;
    private Map<Integer, CFGNode> nodes;

    public abstract void transfer(CFGNode node);

    public abstract LatticeElement meet(Set<LatticeElement> elements);

    public void fixpoint() {
        LinkedList<CFGNode> worklist = new LinkedList<>();
        for (CFGNode node : nodes.values()) {
            worklist.add(node);
        }
        while (!worklist.isEmpty()) {
            // We find the fixpoint using the worklist algorithm
            CFGNode node = worklist.remove();
            LatticeElement oldIn = node.getIn().copy();
            LatticeElement oldOut = node.getOut().copy();
            transfer(node);
            // If the in or out has changed, we add the nodes that could have
            // been affected back to the worklist
            if (!(oldIn.equals(node.getIn()) && oldOut.equals(node.getOut()))) {
                if (direction == Direction.FORWARD) {
                    // If the dataflow is forward, the successors could be affected
                    worklist.addAll(node.getSuccessors());
                } else {
                    // If the dataflow is backward, the predecessors could be affected
                    worklist.addAll(node.getPredecessors());
                }
            }
        }
    }
}
