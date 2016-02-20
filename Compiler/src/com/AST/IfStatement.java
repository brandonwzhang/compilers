package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class IfStatement extends Statement {
    private Expression guard;
    private Block trueBlock;
    private Optional<Block> falseBlock;
}