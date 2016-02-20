package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ArrayIndex extends Expression {
    private Expression id;
    private Expression index;
}
