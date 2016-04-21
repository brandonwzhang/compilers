package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data

public abstract class CFGNode {
    private Set<CFGNode> predecessors = new HashSet<>();
    private Set<CFGNode> successors = new HashSet<>();
    private LatticeElement in;
    private LatticeElement out;

    public void addPredecessor(CFGNode node) {
        predecessors.add(node);
    }

    public void addSuccessor(CFGNode node) {
        successors.add(node);
    }
}
