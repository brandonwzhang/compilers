package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Binary extends Expression {
    private BinaryOperator op;
    private Expression left;
    private Expression right;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
