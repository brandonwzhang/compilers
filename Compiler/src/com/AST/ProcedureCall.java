package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ProcedureCall extends Statement {
    private Identifier identifier;
    private List<Expression> arguments;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
