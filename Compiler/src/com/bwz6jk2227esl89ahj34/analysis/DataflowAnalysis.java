package com.bwz6jk2227esl89ahj34.analysis;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public abstract class DataflowAnalysis {
    public enum Direction {
        FORWARD, BACKWARD
    }

    protected CFG graph;
    // Either the top or bottom element of the lattice
    protected LatticeElement initial;

    public DataflowAnalysis(List<AssemblyLine> lines, Direction direction, LatticeElement initial) {
        this.graph = new CFGAssembly(lines);
        this.initial = initial;
        fixpoint(direction);
    }

    public DataflowAnalysis(List<AssemblyLine> lines, Direction direction, LatticeElement initial, boolean runFixpoint) {
        this.graph = new CFGAssembly(lines);
        this.initial = initial;
        if (runFixpoint) {
            fixpoint(direction);
        }
    }

    public DataflowAnalysis(IRSeq seq, Direction direction, LatticeElement initial) {
        this.graph = new CFGIR(seq);
        this.initial = initial;
        fixpoint(direction);
    }

    public DataflowAnalysis(IRSeq seq, Direction direction, LatticeElement initial, boolean runFixpoint) {
        this.graph = new CFGIR(seq);
        this.initial = initial;
        if (runFixpoint) {
            fixpoint(direction);
        }
    }

    public abstract void transfer(CFGNode node);

    public abstract LatticeElement meet(Set<LatticeElement> elements);

    public void fixpoint(Direction direction) {
        Map<Integer, CFGNode> nodes = graph.getNodes();
        LinkedList<CFGNode> worklist = new LinkedList<>();
        // Initialize the in and out of each node and add it to the worklist
        for (CFGNode node : nodes.values()) {
            // Initialize all in and out to be top
            node.setIn(initial);
            node.setOut(initial);
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

    @Override
    public String toString() {
        return graph.toString();
    }
}
