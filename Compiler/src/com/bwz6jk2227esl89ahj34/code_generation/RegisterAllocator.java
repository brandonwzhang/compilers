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
    public static List<AssemblyLine> translate(List<AssemblyLine> lines) {
        List<AssemblyLine> translatedLines = new LinkedList<>();
        for (AssemblyLine line : lines) {
            // For each instruction we may need to shuttle temps in and out if
            // they spilled onto the stack
            translatedLines.addAll(translateInstruction(line));
        }
        return translatedLines;
    }

    private static List<AssemblyLine> translateInstruction(AssemblyLine line) {
        List<AssemblyLine> lines = new LinkedList<>();

        if (!(line instanceof AssemblyInstruction)) {
            // If line isn't an instruction we don't need to translate any expressions
            lines.add(line);
            return lines;
        }

        AssemblyInstruction instruction = (AssemblyInstruction) line;
        // First, add the original instruction
        lines.add(instruction);

        List<AssemblyExpression> args = instruction.args;
        // Number of spilled temps we've encountered in this instruction so far
        Integer spilledTempNum = new Integer(0);
        // Translate every argument and add any extra lines needed for shuttling
        for (int i = 0; i < args.size(); i++) {
            AssemblyExpression arg = args.get(i);
            if (arg instanceof AssemblyAbstractRegister) {
                AssemblyAbstractRegister register = (AssemblyAbstractRegister) arg;
                translateRegister(register, args, lines, spilledTempNum, i);
            } else if (arg instanceof AssemblyMemoryLocation) {
                AssemblyMemoryLocation location = (AssemblyMemoryLocation) arg;

            }
        }
        return lines;
    }

    private static void translateRegister(AssemblyAbstractRegister register,
                                          List<AssemblyExpression> args,
                                          List<AssemblyLine> lines,
                                          Integer spilledTempNum,
                                          int argNum) {
        // Get the physical location of this temp
        AssemblyExpression mapping = getRegisterMapping(register);
        // Replace the abstract register with its mapping
        if (mapping instanceof AssemblyPhysicalRegister) {
            args.set(argNum, mapping);
            return;
        }

        // The temp was spilled onto the stack, so we need to shuttle it in
        assert mapping instanceof AssemblyMemoryLocation;
        AssemblyMemoryLocation location = (AssemblyMemoryLocation) mapping;
        args.set(argNum, shuttleRegister(location, lines, spilledTempNum));
    }

    private static void translateMemoryLocation() {
        //TODO: Handle abstract register inside memory location
    }

    private static AssemblyExpression shuttleRegister(AssemblyMemoryLocation location,
                                                      List<AssemblyLine> lines,
                                                      Integer spilledTempNum) {
        AssemblyPhysicalRegister[] shuttleRegisters = {AssemblyPhysicalRegister.R13,
                AssemblyPhysicalRegister.R14, AssemblyPhysicalRegister.R15};
        AssemblyPhysicalRegister shuttleRegister = shuttleRegisters[spilledTempNum];
        // Shuttle temp in by adding a move instruction to the beginning
        lines.add(0, new AssemblyInstruction(OpCode.MOVQ,
                location, shuttleRegister));
        // Shuttle temp out by adding a move instruction to the end
        lines.add(new AssemblyInstruction(OpCode.MOVQ,
                shuttleRegister, location));
        spilledTempNum++;
        return shuttleRegister;
    }

    /**
     * Returns the physical location of an abstract register
     */
    private static AssemblyExpression getRegisterMapping(AssemblyAbstractRegister register) {
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
        int stackOffset = AssemblyFunction.getTempSpaceOffset() + Configuration.WORD_SIZE * register.id;
        AssemblyMemoryLocation spillLocation = AssemblyMemoryLocation.stackOffset(stackOffset);
        registerMap.put(register, spillLocation);
        return spillLocation;
    }

    private static AssemblyExpression getArgumentMapping(int id) {
        // If the id is lower than the number of argument registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.argumentRegisters.length) {
            return AssemblyPhysicalRegister.argumentRegisters[id];
        }
        // Return the corresponding stack location
        int stackOffset = AssemblyFunction.getArgumentsOffset() +
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.argumentRegisters.length);
        return AssemblyMemoryLocation.stackOffset(stackOffset);
    }

    private static AssemblyExpression getReturnMapping(int id) {
        // If the id is lower than the number of return registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.returnRegisters.length) {
            return AssemblyPhysicalRegister.returnRegisters[id];
        }
        // Return the corresponding stack location
        int stackOffset = AssemblyFunction.getArgumentsOffset() +
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.returnRegisters.length);
        return AssemblyMemoryLocation.stackOffset(stackOffset);
    }
}
