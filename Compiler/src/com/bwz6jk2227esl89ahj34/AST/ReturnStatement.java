package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ReturnStatement extends Node {
    private List<Expression> values;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
