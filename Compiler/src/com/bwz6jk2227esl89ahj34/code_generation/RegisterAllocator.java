package com.bwz6jk2227esl89ahj34.code_generation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegisterAllocator {
    private Map<AssemblyAbstractRegister, AssemblyExpression> registerMap = new HashMap<>();

    public List<AssemblyInstruction> allocate(List<AssemblyInstruction> instructions) {
        List<AssemblyInstruction> translatedInstructions = new LinkedList<>();
        for (AssemblyInstruction instruction : instructions) {
            translatedInstructions.addAll(translate(instruction));
        }
        return translatedInstructions;
    }

    private List<AssemblyInstruction> translate(AssemblyInstruction instruction) {
        List<AssemblyInstruction> instructions = new LinkedList<>();

        // First, add the original instruction
        instructions.add(instruction);

        List<AssemblyExpression> args = instruction.args;
        for (int i = 0; i < args.size(); i++) {
            AssemblyExpression arg = args.get(i);
            if (arg instanceof AssemblyAbstractRegister) {

            }
        }
        return null;
    }

    private AssemblyExpression getRegisterMapping(AssemblyAbstractRegister register) {
        AssemblyExpression mapping = registerMap.get(register);
        if (mapping != null) {
            assert mapping instanceof AssemblyPhysicalRegister ||
                    mapping instanceof AssemblyMemoryLocation;
            return mapping;
        }
        AssemblyMemoryLocation spillLocation = AssemblyMemoryLocation.stackOffset(AbstractAssemblyGenerator.getScratchSpaceOffset());
    }
}
