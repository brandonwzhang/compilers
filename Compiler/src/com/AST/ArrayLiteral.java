package com.AST;

import lombok.Data;
import java.util.List;

public @Data class ArrayLiteral extends Expression {
    private List<Expression> values;
}
