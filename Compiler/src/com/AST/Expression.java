package com.AST;

public abstract class Expression extends Node {

    public abstract void accept(NodeVisitor v);

}
