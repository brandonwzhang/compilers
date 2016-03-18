package com.bwz6jk2227esl89ahj34.AST;

import com.bwz6jk2227esl89ahj34.AST.type.Type;
import com.bwz6jk2227esl89ahj34.AST.visit.NodeVisitor;
import lombok.Data;

@Data
public abstract class Node {
    private Type type;
    private int row = -1;
    private int col = -1;

    /**
     * Sets this node's location to that of the given node
     * @param node
     */
    public void setLocation(Node node) {
        this.row = (this.row == -1) ? node.getRow(): this.row;
        this.col = (this.col == -1) ? node.getCol(): this.col;
    }

    /**
     * Takes in the parser's cur_token, and saves location data. Only called
     * in the parser.
     * @param symbol
     */
    public void setLocation(java_cup.runtime.Symbol symbol) {
        /*
            Only sets the location once per node. This is done to compensate
            for multi-structures in the grammar that actually get saved to
            one class in our AST. This allows us to store the start of a class
            in the source file, and not the end.
         */
        this.row = (this.row == -1) ? symbol.left : this.row;
        this.col = (this.col == -1) ? symbol.right : this.col;
    }

    public abstract void accept(NodeVisitor v);
}
