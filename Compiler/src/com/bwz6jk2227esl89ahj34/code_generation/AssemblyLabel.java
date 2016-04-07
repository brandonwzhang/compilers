package com.bwz6jk2227esl89ahj34.code_generation;


import java.util.HashSet;

public class AssemblyLabel extends AssemblyInstruction {
    private static HashSet<String> allLabels = new HashSet<String>();
    private String name;

    public AssemblyLabel(String s) {
        assert !(allLabels.contains(s)): "Duplicate labels detected. Please email esl89@cornell.edu";

        allLabels.add(s);
        this.name = s;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
