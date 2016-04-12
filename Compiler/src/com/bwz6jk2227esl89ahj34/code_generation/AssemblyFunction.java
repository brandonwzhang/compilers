package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;


import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.*;

public class AssemblyFunction {
    public static int maxNumReturnValues;
    public static int maxNumArguments;
    public static final int numScratchRegisters = 2;

    private String name;
    private List<AssemblyInstruction> instructions = new LinkedList<>();

    /**
     * Generate abstract assembly code for a single function
     * @param func the IRFuncDecl to be translated to abstract assembly
     * @return
     */
    public AssemblyFunction(IRFuncDecl func) {
        // Save the function name
        name = func.name();

        // Reset the space for abstract registers
        AssemblyAbstractRegister.reset();

        // Generate assembly instructions for this function
        IRSeq seq = (IRSeq) func.body();
        List<AssemblyInstruction> functionBody = new LinkedList<>();
        for (IRStmt statement : seq.stmts()) {
            functionBody.addAll(TileContainer.matchStatement(statement));
        }

        // generateFunctionPrologue needs to be called after the body has been
        // generated to know the number of spilled temps that need to be allocated
        List<AssemblyInstruction> functionPrologue = generateFunctionPrologue();

        // Store the instructions in this instance
        instructions.addAll(functionPrologue);
        // Translate the function body from abstract assembly to assembly
        instructions.addAll(RegisterAllocator.translate(functionBody));
    }

    private static List<AssemblyInstruction> generateFunctionPrologue() {
        List<AssemblyInstruction> instructions = new LinkedList<>();

        // Save old RBP and update RBP
        instructions.add(new AssemblyInstruction(OpCode.PUSHQ, AssemblyPhysicalRegister.RBP));
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RSP, AssemblyPhysicalRegister.RBP));

        /*
                Return Address          (8 byte)
         rbp--> Saved base pointer      (8 byte)
                Callee-saved registers  (6 * 8 bytes)
                Caller-saved registers  (10 * 8 bytes)
                Func extra Return Space (this.maxNumReturnValues * 8 bytes)
                Func extra arg space    (this.maxNumArguments * 8 bytes)
                Scratch space for regs  (this.numScratchRegisters * 8 bytes)
                Space for temps         (variable number)
                Empty alignment         (optional)
         */

        // Represents the "offset" of the base point (rbp - currentStackOffset).
        // Treats
        int currentStackOffset = Configuration.WORD_SIZE;

        // Save callee-save registers rbx rbp r12-r15
        for (int i = 0; i < AssemblyPhysicalRegister.calleeSavedRegisters.length; i++) {
            instructions.add(
                    new AssemblyInstruction(
                            OpCode.MOVQ,
                            AssemblyPhysicalRegister.calleeSavedRegisters[i],
                            AssemblyMemoryLocation.stackOffset(currentStackOffset)
                    )
            );
            currentStackOffset += Configuration.WORD_SIZE;
        }
        // Allocate space for caller-save registers rax, rcx, rsi, rdi, rdx, rsp, r8, r9, r10, r11
        currentStackOffset += Configuration.WORD_SIZE * AssemblyPhysicalRegister.callerSavedRegisters.length;

        // Make space for return values
        currentStackOffset += Configuration.WORD_SIZE * maxNumReturnValues;

        // Make space for arguments
        currentStackOffset += Configuration.WORD_SIZE * maxNumArguments;

        // Make space for scratch registers
        currentStackOffset += Configuration.WORD_SIZE * numScratchRegisters; // RAX, RDX;

        // Make space for temps
        currentStackOffset += Configuration.WORD_SIZE * AssemblyAbstractRegister.numTemps;

        // Make sure stack frame is 16 byte aligned
        if (currentStackOffset % 16 != 0) {
            currentStackOffset += Configuration.WORD_SIZE;
        }

        // Decrement RSP at the beginning of the instructions to make space for everything
        instructions.add(0,
                new AssemblyInstruction(
                        OpCode.SUBQ,
                        new AssemblyImmediate(currentStackOffset - Configuration.WORD_SIZE),
                        AssemblyPhysicalRegister.RSP
                )
        );
        return instructions;
    }

    public static int getCalleeSpaceOffset() {
        return Configuration.WORD_SIZE * (1);
    }
    public static int getCallerSpaceOffset() {
        return getCalleeSpaceOffset() + Configuration.WORD_SIZE * AssemblyPhysicalRegister.calleeSavedRegisters.length;
    }
    public static int getReturnValuesOffset() {
        return getCallerSpaceOffset() + Configuration.WORD_SIZE * AssemblyPhysicalRegister.callerSavedRegisters.length;
    }

    public static int getArgumentsOffset() {
        return getReturnValuesOffset() + Configuration.WORD_SIZE * maxNumReturnValues;
    }

    public static int getScratchSpaceOffset() {
        return getArgumentsOffset() + Configuration.WORD_SIZE * maxNumArguments;
    }

    public static int getTempSpaceOffset() {
        return getScratchSpaceOffset() + Configuration.WORD_SIZE * numScratchRegisters;
    }

    @Override
    public String toString() {
        String s = "";
        s += "\t\t.globl\tFUNC(" + name + ")\n";
        s += "\t\t.align\t4\n";
        s += "FUNC(" + name + "):\n";
        for (AssemblyInstruction instruction : instructions) {
            if (instruction instanceof AssemblyLabel) {
                s += instruction + "\n";
            } else {
                s += "\t\t" + instruction + "\n";
            }
        }
        return s;
    }
}
