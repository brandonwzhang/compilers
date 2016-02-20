package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WhileStatement extends Statement {
    private Expression guard;
    private Block block;
}
