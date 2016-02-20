package com.AST;

import lombok.Data;

import java.util.List;

public @Data class FunctionBlock extends Block {
    private List<Block> blocks;
    private ReturnStatement returnStatement;
}
