package com.bwz6jk2227esl89ahj34.ast.parse;

import com.bwz6jk2227esl89ahj34.ast.ClassDeclaration;
import com.bwz6jk2227esl89ahj34.ast.FunctionDeclaration;
import com.bwz6jk2227esl89ahj34.ast.UseStatement;
import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Interface {
    private List<UseStatement> useBlock;
    private List<FunctionDeclaration> functionDeclarations;
    private List<ClassDeclaration> classDeclarations;

    // Default constructor
    public Interface() {
        this.useBlock = new LinkedList<>();
        this.functionDeclarations = new LinkedList<>();
        this.classDeclarations = new LinkedList<>();
    }

    public void set(Interface i) {
        this.useBlock = i.getUseBlock();
        this.functionDeclarations = i.getFunctionDeclarations();
        this.classDeclarations = i.getClassDeclarations();
    }

}
