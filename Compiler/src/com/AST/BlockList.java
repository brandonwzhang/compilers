package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class BlockList extends Block {
    private List<Block> blockList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
