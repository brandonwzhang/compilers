package com.AST;

import lombok.Data;

public @Data class ArrayIndex extends Expression {
    private Expression id;
    private Expression index;
}
