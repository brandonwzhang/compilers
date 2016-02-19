package com.AST;

import lombok.Data;

public @Data class IfStatement extends Statement {
    private Expression guard;
    private Block block;
}