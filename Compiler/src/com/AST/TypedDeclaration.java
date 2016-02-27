package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TypedDeclaration extends Statement implements Assignable {
    private Identifier identifier;
    private Type.PrimitiveType primitiveType;
    private List<Expression> arraySized;
    private Integer arrayEmpty;

    //private Type type;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}