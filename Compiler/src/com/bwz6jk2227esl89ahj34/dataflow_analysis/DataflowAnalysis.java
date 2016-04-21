package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DataflowAnalysis {
    public enum Direction {
        FORWARD, BACKWARD
    }

    private CFG graph;

    public DataflowAnalysis(List<AssemblyLine> lines, Direction direction) {
        this.graph = new CFGAssembly(lines);
        fixpoint(direction);
    }

    public DataflowAnalysis(IRSeq seq, Direction direction) {
        this.graph = new CFGIR(seq);
        fixpoint(direction);
    }

    public abstract void transfer(CFGNode node);

    public abstract LatticeElement meet(Set<LatticeElement> elements);

    public void fixpoint(Direction direction) {
        Map<Integer, CFGNode> nodes = graph.getNodes();
        LinkedList<CFGNode> worklist = new LinkedList<>();
        // Initialize the in and out of each node and add it to the worklist
        for (CFGNode node : nodes.values()) {
            // Initialize all in and out to be top
            node.setIn(new LatticeTop());
            node.setOut(new LatticeTop());
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
