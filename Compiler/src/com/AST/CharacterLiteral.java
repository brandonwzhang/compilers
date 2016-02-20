package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class CharacterLiteral extends Expression {
    private char value;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
