package com.AST;

import lombok.Data;
import java.util.List;

public @Data class Expression extends Node {

    public static @Data class Binary extends Expression {
        private BinaryOperator op;
        private Expression left;
        private Expression right;
    }

    public static @Data class Unary extends Expression {
        private UnaryOperator op;
        private Expression expr;
    }

    public static @Data class IntegerLiteral extends Expression {
        private int value;
    }

    public static @Data class BooleanLiteral extends Expression {
        private boolean value;
    }

    public static @Data class CharacterLiteral extends Expression {
        private char value;
    }

    public static @Data class StringLiteral extends Expression {
        private String value;
    }

    public static @Data class ArrayLiteral extends Expression {
        List<Expression> values;
    }

    public static @Data class ArrayIndex extends Expression {
        Expression id;
        Expression index;
    }

    public static @Data class FunctionCall extends Expression {
        private Identifier identifier;
        private List<Expression> arguments;
    }
}
