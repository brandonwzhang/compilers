package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Unary extends Expression {
    private UnaryOperator op;
    private Expression expr;
}
