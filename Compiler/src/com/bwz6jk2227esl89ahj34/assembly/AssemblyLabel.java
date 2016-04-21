package com.bwz6jk2227esl89ahj34.assembly;


import lombok.Data;
import lombok.EqualsAndHashCode;

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
