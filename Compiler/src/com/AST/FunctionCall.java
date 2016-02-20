package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@AllArgsConstructor
@Data
public class FunctionCall extends Expression {
    private Identifier identifier;
    private List<Expression> arguments;
}
