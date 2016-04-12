package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegisterAllocator {
    private static Map<AssemblyAbstractRegister, AssemblyExpression> registerMap = new HashMap<>();

    /**
     * Returns a list of instructions with all abstract registers with physical locations
     */
    public List<AssemblyInstruction> translate(List<AssemblyInstruction> instructions) {
        List<AssemblyInstruction> translatedInstructions = new LinkedList<>();
        for (AssemblyInstruction instruction : instructions) {
            // For each instruction we may need to shuttle temps in and out if
            // they spilled onto the stack
            translatedInstructions.addAll(translateInstruction(instruction));
        }
        return translatedInstructions;
    }

    private List<AssemblyInstruction> translateInstruction(AssemblyInstruction instruction) {
        List<AssemblyInstruction> instructions = new LinkedList<>();

        // First, add the original instruction
        instructions.add(instruction);

        List<AssemblyExpression> args = instruction.args;
        // Number of spilled temps we've encountered in this instruction so far
        int numSpilledTemps = 0;
        // The registers we'll use to shuttle temps in and out
        // We only need 3
        AssemblyPhysicalRegister[] shuttleRegisters = {AssemblyPhysicalRegister.R13,
                AssemblyPhysicalRegister.R14, AssemblyPhysicalRegister.R15};
        // Translate every argument and add any extra instructions needed for shuttling
        for (int i = 0; i < args.size(); i++) {
            AssemblyExpression arg = args.get(i);
            if (arg instanceof AssemblyAbstractRegister) {
                AssemblyAbstractRegister register = (AssemblyAbstractRegister) arg;
                // Get the physical location of this temp
                AssemblyExpression mapping = getRegisterMapping(register);
                // Replace the abstract register with its mapping
                args.set(i, mapping);

                if (mapping instanceof AssemblyPhysicalRegister) {
                    // If it maps to a register, we don't need to add any extra instructions
                    instructions.add(instruction);
                    continue;
                }
                // The temp was spilled onto the stack, so we need to shuttle it in
                assert mapping instanceof AssemblyMemoryLocation;
                AssemblyMemoryLocation location = (AssemblyMemoryLocation) mapping;
                // Shuttle temp in by adding a move instruction to the beginning
                instructions.add(0, new AssemblyInstruction(OpCode.MOVQ,
                        location, shuttleRegisters[numSpilledTemps++]));
                // Shuttle temp out by adding a move instruction to the end
                instructions.add(new AssemblyInstruction(OpCode.MOVQ,
                        shuttleRegisters[numSpilledTemps++], location));
            }
        }
        return instructions;
    }

    /**
     * Returns the physical location of an abstract register
     */
    private AssemblyExpression getRegisterMapping(AssemblyAbstractRegister register) {
        if (register.isArgument) {
            return getArgumentMapping(register.id);
        }
        if (register.isReturn) {
            return getReturnMapping(register.id);
        }
        AssemblyExpression mapping = registerMap.get(register);
        if (mapping != null) {
            assert mapping instanceof AssemblyPhysicalRegister ||
                    mapping instanceof AssemblyMemoryLocation;
            return mapping;
        }
        // Naive register allocation: we assign each temp a location on the stack corresponding to its id
        AssemblyMemoryLocation spillLocation = AssemblyMemoryLocation.stackOffset(
                AbstractAssemblyGenerator.getTempSpaceOffset() + Configuration.WORD_SIZE * register.id);
        registerMap.put(register, spillLocation);
        return spillLocation;
    }

    private AssemblyExpression getArgumentMapping(int id) {
        // If the id is lower than the number of argument registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.argumentRegisters.length) {
            return AssemblyPhysicalRegister.argumentRegisters[id];
        }
        // Return the corresponding stack location
        int stackOffset = AbstractAssemblyGenerator.getArgumentsOffset() +
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.argumentRegisters.length);
        return AssemblyMemoryLocation.stackOffset(stackOffset);
    }

    private AssemblyExpression getReturnMapping(int id) {
        // If the id is lower than the number of return registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.returnRegisters.length) {
            return AssemblyPhysicalRegister.returnRegisters[id];
        }
        // Return the corresponding stack location
        int stackOffset = AbstractAssemblyGenerator.getArgumentsOffset() +
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.returnRegisters.length);
        return AssemblyMemoryLocation.stackOffset(stackOffset);
    }
}
