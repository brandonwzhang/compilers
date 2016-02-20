package com.AST;

public class Underscore extends Node implements Assignable {

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}
