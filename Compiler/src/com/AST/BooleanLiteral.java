package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class BooleanLiteral extends Expression {
    private Boolean value;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
