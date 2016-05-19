package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.type.FunctionType;
import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private FunctionType functionType;
    private List<Identifier> argumentIdentifiers;
    private BlockList blockList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}