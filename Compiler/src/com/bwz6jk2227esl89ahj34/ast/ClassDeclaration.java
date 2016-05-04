package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassDeclaration extends Node {
    private Identifier identifier;
    private List<TypedDeclaration> fields;
    private List<MethodDeclaration> methods;
    private Optional<Identifier> parentIdentifier;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
