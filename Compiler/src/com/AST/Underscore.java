package com.AST;

public class Underscore implements Assignable {

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}
