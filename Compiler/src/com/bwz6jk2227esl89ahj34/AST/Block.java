package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;

public abstract class Block extends Node {

    public abstract void accept(NodeVisitor v);
}
