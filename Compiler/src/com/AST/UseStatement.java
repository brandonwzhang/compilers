package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class UseStatement {
    private Identifier identifier;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
