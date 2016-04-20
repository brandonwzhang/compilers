package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data

public class CFGNode {
    private Set<CFGNode> predecessors = new HashSet<>();
    private Set<CFGNode> successors = new HashSet<>();
    private LatticeElement in;
    private LatticeElement out;

    private AssemblyInstruction assemblyInstruction;
    private IRStmt irstmt;
    int index;
    private NodeType nodeType;

    public enum NodeType {
        ASSEMBLY, IR
    }

    public CFGNode(AssemblyInstruction instruction, int index) {
        this.assemblyInstruction = instruction;
        this.index = index;
        this.nodeType = NodeType.ASSEMBLY;
    }

    public CFGNode(IRStmt stmt, int index) {
        this.irstmt = stmt;
        this.index = index;
        this.nodeType = NodeType.IR;
    }

    public void addPredecessor(CFGNode node) {
        predecessors.add(node);
    }

    public void addSuccessor(CFGNode node) {
        successors.add(node);
    }
}
