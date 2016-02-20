package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Binary extends Expression {
    private BinaryOperator op;
    private Expression left;
    private Expression right;
}
