package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Program extends Node {
    private List<UseStatement> useBlock;
    private List<Assignment> globalVariables;
    private List<FunctionDeclaration> functionDeclarations;
    private List<ClassDeclaration> classDeclarations;

    public void accept(NodeVisitor v){ v.visit(this); }
}
