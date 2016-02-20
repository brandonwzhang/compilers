package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private List<TypedDeclaration> typedDeclarationList;
    private List<Type> typeList;
    private MethodBlock functionBlock;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}