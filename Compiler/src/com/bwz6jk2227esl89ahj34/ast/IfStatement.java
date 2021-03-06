package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class IfStatement extends Statement {
    private Expression guard;
    private Block trueBlock;
    private Optional<Block> falseBlock;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}