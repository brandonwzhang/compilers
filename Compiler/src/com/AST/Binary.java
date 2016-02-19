package com.AST;

import lombok.Data;

public @Data class Binary extends Expression {
    private BinaryOperator op;
    private Expression left;
    private Expression right;
}
