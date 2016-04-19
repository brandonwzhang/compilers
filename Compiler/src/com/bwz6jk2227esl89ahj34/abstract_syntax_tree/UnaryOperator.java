package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;


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
