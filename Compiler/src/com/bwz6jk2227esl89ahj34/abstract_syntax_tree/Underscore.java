package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit.NodeVisitor;

public class Underscore extends Node implements Assignable {

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}
