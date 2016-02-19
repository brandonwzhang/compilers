package com.AST;

import lombok.Data;

import java.util.List;

public @Data class Block extends Node {
    public static @Data class BlockList extends Block {
        private List<Block> blockList;
    }
    public static @Data class FunctionBlock extends Block {
        private List<Block> blocks;
        private ReturnStatement returnStatement;
    }
}
