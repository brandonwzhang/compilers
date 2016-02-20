package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ProcedureBlock extends Block {
    private BlockList blockList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
