package com.bwz6jk2227esl89ahj34.code_generation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegisterAllocator {
    public List<AssemblyInstruction> allocate(List<AssemblyInstruction> instructions) {
        Map<AssemblyAbstractRegister, AssemblyExpression> registerMap =
                new HashMap<>();
        List<AssemblyInstruction> translatedInstructions = new LinkedList<>();
        for (AssemblyInstruction instruction : instructions) {
            translatedInstructions.addAll(translate(instruction, registerMap));
        }
        return translatedInstructions;
    }

    public List<AssemblyInstruction> translate(AssemblyInstruction instruction,
                                               Map<AssemblyAbstractRegister, AssemblyExpression> registerMap) {

        return null;
    }
}
