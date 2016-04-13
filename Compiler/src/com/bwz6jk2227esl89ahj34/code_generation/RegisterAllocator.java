package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegisterAllocator {
    private static Map<AssemblyAbstractRegister, AssemblyExpression> registerMap = new HashMap<>();
    // Counts the number of spilled temps that have been encountered in an instruction
    // For each instruction, this should be reset to 0
    private static int numSpilledTemps = 0;

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
        numSpilledTemps = 0;
        // Translate every argument and add any extra lines needed for shuttling
        for (int i = 0; i < args.size(); i++) {
            AssemblyExpression arg = args.get(i);
            if (arg instanceof AssemblyAbstractRegister) {
                AssemblyAbstractRegister register = (AssemblyAbstractRegister) arg;
                args.set(i, translateRegister(register, lines));
            } else if (arg instanceof AssemblyMemoryLocation) {
                AssemblyMemoryLocation location = (AssemblyMemoryLocation) arg;
                args.set(i, translateMemoryLocation(location, lines));
            }
        }
        return lines;
    }

    /**
     * Translate an abstract register to a physical one
     */
    private static AssemblyPhysicalRegister translateRegister(AssemblyAbstractRegister register,
                                                              List<AssemblyLine> lines) {
        // Get the physical location of this temp
        AssemblyExpression mapping = getRegisterMapping(register);
        // Replace the abstract register with its mapping
        if (mapping instanceof AssemblyPhysicalRegister) {
            return (AssemblyPhysicalRegister) mapping;
        }

        // The temp was spilled onto the stack, so we need to shuttle it in
        assert mapping instanceof AssemblyMemoryLocation;
        AssemblyMemoryLocation location = (AssemblyMemoryLocation) mapping;
        return shuttleRegister(location, lines);
    }

    /**
     * Translate a memory location to contain no abstract registers
     */
    private static AssemblyMemoryLocation translateMemoryLocation(AssemblyMemoryLocation location,
                                                                  List<AssemblyLine> lines) {
        // Translate base and offset register if they're abstract registers
        if (location.baseRegister instanceof AssemblyAbstractRegister) {
            location.baseRegister =
                    translateRegister((AssemblyAbstractRegister) location.baseRegister, lines);
        }
        if (location.offsetRegister instanceof AssemblyAbstractRegister) {
            location.offsetRegister =
                    translateRegister((AssemblyAbstractRegister) location.offsetRegister, lines);
        }
        return location;
    }

    /**
     * Add instructions to shuttle a temp in and out
     */
    private static AssemblyPhysicalRegister shuttleRegister(AssemblyMemoryLocation location,
                                                            List<AssemblyLine> lines) {
        AssemblyPhysicalRegister[] shuttleRegisters = {AssemblyPhysicalRegister.R13,
                AssemblyPhysicalRegister.R14, AssemblyPhysicalRegister.R15};
        AssemblyPhysicalRegister shuttleRegister = shuttleRegisters[numSpilledTemps];
        // Shuttle temp in by adding a move instruction to the beginning
        lines.add(0, new AssemblyInstruction(OpCode.MOVQ,
                location, shuttleRegister));
        // Shuttle temp out by adding a move instruction to the end
        lines.add(new AssemblyInstruction(OpCode.MOVQ,
                shuttleRegister, location));
        numSpilledTemps++;
        return shuttleRegister;
    }

    /**
     * Returns the physical location of an abstract register
     */
    private static AssemblyExpression getRegisterMapping(AssemblyAbstractRegister register) {
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
}
