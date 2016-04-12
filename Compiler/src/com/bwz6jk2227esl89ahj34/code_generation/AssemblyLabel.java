package com.bwz6jk2227esl89ahj34.code_generation;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;

@Data
@EqualsAndHashCode
public class AssemblyLabel extends AssemblyLine {
    private static HashSet<String> allLabels = new HashSet<String>();
    private AssemblyName name;

    public AssemblyLabel(AssemblyName name) {
        assert !(allLabels.contains(name.getName())): "Duplicate labels detected. Please email esl89@cornell.edu";

        allLabels.add(name.getName());
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
