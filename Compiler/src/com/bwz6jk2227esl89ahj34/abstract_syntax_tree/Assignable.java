package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type.Type;
import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit.NodeVisitor;

public interface Assignable {
    void accept(NodeVisitor visitor);
    Type getType();
    void setLocation(java_cup.runtime.Symbol symbol);
}
