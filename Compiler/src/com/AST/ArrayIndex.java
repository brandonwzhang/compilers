package com.AST;

import lombok.Data;

public @Data class ArrayIndex extends Expression {
    Expression id;
    Expression index;
}
