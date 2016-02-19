package com.AST;

import lombok.Data;

public @Data class IfElseStatement extends Statement {
    private Expression guard;
    private Block trueBlock;
    private Block falseBlock;
}
