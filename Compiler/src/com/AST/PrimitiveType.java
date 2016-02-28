package com.AST;

public enum PrimitiveType {
    INT, BOOL, UNIT, VOID;

    @Override public String toString() {
        return super.toString().toLowerCase();
    }
}
