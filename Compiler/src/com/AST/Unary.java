package com.AST;

import lombok.Data;

public @Data class Unary extends Expression {
    private UnaryOperator op;
    private Expression expr;
}
