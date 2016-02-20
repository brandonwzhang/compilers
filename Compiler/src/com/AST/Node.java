package com.AST;

public abstract class Node {

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
