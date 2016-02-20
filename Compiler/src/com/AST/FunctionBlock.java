package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class FunctionBlock extends Block {
    private BlockList blockList;
    private ReturnStatement returnStatement;
}
