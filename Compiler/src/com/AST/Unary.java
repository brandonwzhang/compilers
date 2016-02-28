package com.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Unary extends Expression {
    private UnaryOperator op;
    private Expression expression;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
