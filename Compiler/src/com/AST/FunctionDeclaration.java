package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.AbstractMap;
import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionDeclaration {
    private Identifier identifier;
    private FunctionType type;
    private List<Identifier> argList;
    private MethodBlock methodBlock;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}