package com.bwz6jk2227esl89ahj34.analysis;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction;
import lombok.Getter;

@Getter
public class CFGNodeAssembly extends CFGNode {
    private AssemblyInstruction instruction;

    public CFGNodeAssembly(AssemblyInstruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        String label = "" + instruction + "\\n";
        return label + super.toString();
    }
}
