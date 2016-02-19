package com.AST;

/**
 * Created by eric on 2/18/16.
 */
public abstract class Node {

    public Node(){

    }

    public void accept(Visitor v){
        v.preVisit(this);
        v.visit(this);
        v.postVisit(this);
    }
}
