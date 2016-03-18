package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayIndex extends Expression implements Assignable {
    private Expression arrayRef;
    private Expression index;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
