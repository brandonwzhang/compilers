package com.AST;

import lombok.Data;

public @Data class WhileStatement extends Statement {
    private Expression guard;
    private Block block;
}
