package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CFGNode<T, E> {
    List<CFGNode> predecessors;
    List<CFGNode> successors;

    T data;
    Set<E> in;
    Set<E> out;
}