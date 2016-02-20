package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class BooleanType extends Type {

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
