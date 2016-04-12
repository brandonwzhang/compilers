package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.parse.InterfaceParser;
import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;
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
    private List<FunctionDeclaration> functionDeclarationList;

    public void accept(NodeVisitor v){ v.visit(this); }

    public List<FunctionDeclaration> getFunctionsFromUseStatement(String libPath) {
        List<FunctionDeclaration> declarations = new LinkedList<>();
        for (UseStatement useStatement : useBlock) {
            String error = InterfaceParser.parseInterface(libPath,
                    useStatement.getIdentifier().getName(), declarations);
        }
        return declarations;
    }
}
