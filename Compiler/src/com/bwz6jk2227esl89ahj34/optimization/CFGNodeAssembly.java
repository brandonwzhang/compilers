package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CFGNodeAssembly extends CFGNode {
    private AssemblyInstruction instruction;

    public CFGNodeAssembly(AssemblyInstruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "" + instruction;
    }
}
