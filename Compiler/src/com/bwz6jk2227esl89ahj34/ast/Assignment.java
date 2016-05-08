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
public class Assignment extends Statement {
    private List<Assignable> variables;
    private Expression expression;

    public Assignment(Assignable variable, Expression expression) {
        variables = new LinkedList<>();
        variables.add(variable);
        this.expression = expression;
    }

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
