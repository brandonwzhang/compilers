package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class FunctionBlock extends Block {
    private List<Block> blocks;
    private ReturnStatement returnStatement;
}
