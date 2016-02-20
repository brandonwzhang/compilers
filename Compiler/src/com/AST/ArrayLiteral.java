package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayLiteral extends Expression {
    private List<Expression> values;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
