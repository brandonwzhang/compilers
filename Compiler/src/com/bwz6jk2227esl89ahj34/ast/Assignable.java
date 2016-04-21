package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.type.Type;
import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;

public interface Assignable {
    void accept(NodeVisitor visitor);
    Type getType();
    void setLocation(java_cup.runtime.Symbol symbol);
}
