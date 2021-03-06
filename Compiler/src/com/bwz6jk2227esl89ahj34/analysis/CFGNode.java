package com.bwz6jk2227esl89ahj34.analysis;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public abstract class CFGNode {
    private List<CFGNode> predecessors = new LinkedList<>();
    private List<CFGNode> successors = new LinkedList<>();
    private LatticeElement in;
    private LatticeElement out;

    public void addPredecessor(CFGNode node) {
        if (!predecessors.contains(node)) {
            predecessors.add(node);
        }
    }

    public void addSuccessor(CFGNode node) {
        if (!successors.contains(node)) {
            successors.add(node);
        }
    }

    @Override
    public String toString() {
        String s = "";
        s += "in: " + in + "\\n";
        s += "out: " + out + "\\n";
        return s;
    }
}
