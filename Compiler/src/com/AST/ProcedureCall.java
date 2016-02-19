package com.AST;

import lombok.Data;
import java.util.List;

public @Data class ProcedureCall extends Statement {
    private Identifier identifier;
    private List<Expression> arguments;
}
