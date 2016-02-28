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
    private VariableType type;
    private List<Expression> arraySizeList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}