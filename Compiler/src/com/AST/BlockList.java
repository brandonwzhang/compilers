package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class BlockList extends Block {
    private List<Block> blockList;
}
