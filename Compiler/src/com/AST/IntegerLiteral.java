package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IntegerLiteral extends Expression {
    private String value;
}
