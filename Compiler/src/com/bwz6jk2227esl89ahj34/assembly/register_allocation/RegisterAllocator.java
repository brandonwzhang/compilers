package com.bwz6jk2227esl89ahj34.assembly.register_allocation;

import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables
        .LiveVariableAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables
        .LiveVariableSet;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.util.*;

public class RegisterAllocator {
    public static final AssemblyPhysicalRegister[] shuttleRegisters = {AssemblyPhysicalRegister.R13,
            AssemblyPhysicalRegister.R14, AssemblyPhysicalRegister.R15};
    private static Map<AssemblyAbstractRegister, AssemblyExpression> registerMap;
    // Counts the number of spilled temps that have been encountered in an instruction
    // For each instruction, this should be reset to 0
    private static int shuttleRegisterIndex;
    // Number of spilled temps we need for this function
    public static int numSpilledTemps;

    /**
     * Returns a list of instructions with all abstract registers with physical locations
     */
    public static List<AssemblyLine> translate(List<AssemblyLine> lines) {
        // Perform live variable analysis to construct interference sets
        LiveVariableAnalysis liveVariables = new LiveVariableAnalysis(lines);
        Util.writeHelper(
                "live_variables_",
                "dot",
                "./",
                Collections.singletonList(liveVariables.toString())
        );

        List<Set<AssemblyAbstractRegister>> interferenceSets = new LinkedList<>();
        for (CFGNode node : liveVariables.getGraph().getNodes().values()) {
            LatticeElement element = node.getOut();
            interferenceSets.add(((LiveVariableSet) element).getLiveVars());
        }
        // Use Kempe's algorithm to allocate physical locations to abstract registers
        GraphColorer graphColorer = new GraphColorer(interferenceSets, lines);
        registerMap = graphColorer.getColoring();
        // Translate all the instructions
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
        shuttleRegisterIndex = 0;
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
        AssemblyPhysicalRegister shuttleRegister = shuttleRegisters[shuttleRegisterIndex];
        // Shuttle temp in by adding a move instruction to the beginning
        lines.add(0, new AssemblyInstruction(OpCode.MOVQ,
                location, shuttleRegister));
        // Shuttle temp out by adding a move instruction to the end
        lines.add(new AssemblyInstruction(OpCode.MOVQ,
                shuttleRegister, location));
        shuttleRegisterIndex++;
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
        // If there's no mapping we spill the temp onto the stack
        int stackOffset = AssemblyFunction.getTempSpaceOffset() + Configuration.WORD_SIZE * numSpilledTemps++;
        AssemblyMemoryLocation spillLocation = AssemblyMemoryLocation.stackOffset(stackOffset);
        registerMap.put(register, spillLocation);
        return spillLocation;
    }
}
