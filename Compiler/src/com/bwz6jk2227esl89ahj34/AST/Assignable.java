package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;

public interface Assignable {
    void accept(NodeVisitor visitor);
    Type getType();
    void setLocation(java_cup.runtime.Symbol symbol);
}
