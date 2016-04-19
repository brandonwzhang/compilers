package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.HashSet;
import java.util.List;

public class CFGNode<T, E> {
    List<CFGNode> predecessors;
    List<CFGNode> successors;

    T data;
    HashSet<E> in;
    HashSet<E> out;
}
