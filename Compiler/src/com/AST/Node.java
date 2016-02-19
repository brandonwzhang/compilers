package com.AST;

public abstract class Node {

    public void accept(NodeVisitor v){
        v.preVisit(this);
        v.visit(this);
        v.postVisit(this);
    }
}
