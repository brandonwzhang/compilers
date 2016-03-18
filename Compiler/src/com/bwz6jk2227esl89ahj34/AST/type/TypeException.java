package com.bwz6jk2227esl89ahj34.AST.type;

public class TypeException extends RuntimeException {
    private int row;
    private int col;
    private String errMessage;

    public TypeException(String message) {
        super(message);
    }

    public TypeException(String message, int row, int col) {
        this.errMessage = message;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return row + ":" + col + " error:" + this.errMessage;
    }
}
