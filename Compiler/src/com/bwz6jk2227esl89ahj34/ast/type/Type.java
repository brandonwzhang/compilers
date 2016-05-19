package com.bwz6jk2227esl89ahj34.ast.type;

public abstract class Type {

    public boolean isUnit() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isBool() {
        return false;
    }

    public boolean isNullable() {
        return false;
    }
}