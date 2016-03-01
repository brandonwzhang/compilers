package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ProcedureBlock extends MethodBlock {
    private BlockList blockList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
