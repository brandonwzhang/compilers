package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;

public class Underscore extends Node implements Assignable {

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}