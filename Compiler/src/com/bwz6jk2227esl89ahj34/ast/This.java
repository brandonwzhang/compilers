package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.type.ClassType;
import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;

public class This extends Expression {
    public This() {}

    public This(ClassType type) {
        setType(type);
    }

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
