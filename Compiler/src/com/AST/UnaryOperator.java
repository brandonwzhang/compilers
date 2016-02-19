package com.AST;


public enum UnaryOperator {
    MINUS("-"),
    NOT("!");

    String print;

    UnaryOperator(String print) {
        this.print = print;
    }

    public String toString() {
        return print;
    }
}
