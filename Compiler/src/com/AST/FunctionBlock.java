package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionBlock extends MethodBlock {
    private BlockList blockList;
    private ReturnStatement returnStatement;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
