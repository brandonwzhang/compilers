package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionCall extends Expression implements Assignable {
    private Identifier identifier;
    private List<Expression> arguments;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
