package com.AST;

import com.bwz6jk2227esl89ahj34.NodeVisitor;
import lombok.Data;

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

    public abstract void accept(NodeVisitor v);
}
