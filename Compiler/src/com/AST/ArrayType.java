package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayType extends Type {
    private Type baseType;
    private Optional<Expression> size;

    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
