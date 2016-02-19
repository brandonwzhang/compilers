package com.AST;

public enum BinaryOperator {
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),
    MODULO("%"),
    HIGH_MULT("*>>"),
    AND("&"),
    OR("|"),
    EQUAL("=="),
    NOT_EQUAL("!="),
    LT("<"),
    LEQ("<="),
    GT(">"),
    GEQ(">=");

    String print;

    BinaryOperator(String print) {
        this.print = print;
    }

    public String toString() {
        return print;
    }
}
