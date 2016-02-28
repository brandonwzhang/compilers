package com.AST;

import lombok.Data;
import java_cup.runtime.*;

@Data
public abstract class Node {
    private Type type;
    private int row;
    private int col;

    /**
     * Takes in the parser's cur_token, and extract location data. Only called
     * in the parser.
     * @param symbol
     */
    public void setLocation(java_cup.runtime.Symbol symbol) {
        this.row = symbol.left;
        this.col = symbol.right;
    }
}
