package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;

public abstract class Block extends Node {

    public abstract void accept(NodeVisitor v);
}
