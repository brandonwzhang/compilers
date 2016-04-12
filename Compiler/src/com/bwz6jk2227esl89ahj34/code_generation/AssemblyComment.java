package com.bwz6jk2227esl89ahj34.code_generation;

public class AssemblyComment extends AssemblyInstruction {
    private String comment;
    public AssemblyComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "#"+comment;
    }
}
