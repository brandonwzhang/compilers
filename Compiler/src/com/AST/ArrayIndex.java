package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayIndex extends Expression implements Assignable {
    private Expression arrayRef;
    private Expression index;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
