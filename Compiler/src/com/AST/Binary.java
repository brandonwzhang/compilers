package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Binary extends Expression {
    private BinaryOperator op;
    private Expression left;
    private Expression right;
}
