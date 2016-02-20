package com.AST;

import lombok.Data;
import java.util.List;

public @Data class BlockList extends Block {
    private List<Block> blockList;
}
