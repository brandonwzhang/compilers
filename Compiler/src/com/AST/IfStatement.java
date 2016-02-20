package com.AST;

import lombok.Data;

import java.util.Optional;

public @Data class IfStatement extends Statement {
    private Expression guard;
    private Block trueBlock;
    private Optional<Block> falseBlock;
}