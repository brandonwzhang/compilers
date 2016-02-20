package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BooleanLiteral extends Expression {
    private boolean value;
}
