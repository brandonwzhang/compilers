package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.ir.IRNode;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data

public class CFGNode<T> {
    private Set<CFGNode> predecessors;
    private Set<CFGNode> successors;

    private T data;

    private LatticeElement in;
    private LatticeElement out;

}
