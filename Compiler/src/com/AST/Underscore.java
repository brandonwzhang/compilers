package com.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;

public class Underscore implements Assignable {

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}
