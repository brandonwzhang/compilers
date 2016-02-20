package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@AllArgsConstructor
@Data
public class ProcedureCall extends Statement {
    private Identifier identifier;
    private List<Expression> arguments;
}
