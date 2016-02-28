package com.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Program extends Node {
    private List<UseStatement> useBlock;
    private List<FunctionDeclaration> funcDecs;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
