package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.AbstractMap;
import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private FunctionType functionType;
    private List<Identifier> argList;
    private MethodBlock methodBlock;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}