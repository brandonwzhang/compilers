package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class ArrayLiteral extends Expression {
    private List<Expression> values;
}
