package com.bwz6jk2227esl89ahj34.assembly;

public class AssemblyComment extends AssemblyLine {
    private String comment;
    public AssemblyComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "# " + comment.replace("\n", "\n#");
    }

}
