package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class WhileStatement extends Statement {
    private Expression guard;
    private Block block;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
