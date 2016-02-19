package com.AST;

import lombok.Data;

import java.util.List;

public @Data class FunctionCall extends Expression {
    private Identifier identifier;
    private List<Expression> arguments;
}
