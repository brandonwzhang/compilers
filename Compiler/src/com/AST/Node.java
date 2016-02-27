package com.AST;

import lombok.Data;

@Data
public abstract class Node {
    private Type type;
    private int row;
    private int col;
}
