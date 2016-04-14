package com.bwz6jk2227esl89ahj34.code_generation;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyLabel extends AssemblyLine {
    private AssemblyName name;

    public AssemblyLabel(AssemblyName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
