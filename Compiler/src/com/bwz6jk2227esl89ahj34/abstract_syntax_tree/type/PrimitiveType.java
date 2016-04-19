package com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type;

public enum PrimitiveType {
    INT, BOOL, UNIT, VOID;

    @Override public String toString() {
        return super.toString().toLowerCase();
    }
}
