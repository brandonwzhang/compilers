package com.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;

public interface Assignable {
    void accept(NodeVisitor visitor);
    Type getType();
}
