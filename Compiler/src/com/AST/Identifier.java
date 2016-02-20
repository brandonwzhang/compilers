package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Identifier extends Expression implements Assignable {
    private String name;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
