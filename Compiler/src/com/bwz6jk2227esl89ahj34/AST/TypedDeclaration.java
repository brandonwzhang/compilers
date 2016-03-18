package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.type.VariableType;
import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TypedDeclaration extends Statement implements Assignable {
    private Identifier identifier;
    private VariableType declarationType;
    private List<Expression> arraySizeList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}