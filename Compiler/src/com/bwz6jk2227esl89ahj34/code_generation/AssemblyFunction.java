package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.code_generation.tiles.TileContainer;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;


import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.*;

public class AssemblyFunction {
    public static int maxNumReturnValues;
    public static int maxNumArguments;
    public static final int numScratchRegisters = 2;
    // Number of bytes to pad onto stack frame to make it 16 byte aligned
    public static int padding;

    private String name;
    private List<String> global;
    private List<AssemblyLine> lines = new LinkedList<>();

    /**
     * Generate abstract assembly code for a single function
     * @param func the IRFuncDecl to be translated to abstract assembly
     * @return
     */
    public AssemblyFunction(IRFuncDecl func, List<String> global) {
        // Save the function name
        name = func.name();

        // the name of functions introduced by the use statement is set as global
        this.global = global;


        // Reset the space for abstract registers
        AssemblyAbstractRegister.reset();

        // Generate assembly lines for this function
        IRSeq seq = (IRSeq) func.body();
        List<AssemblyLine> functionBody = new LinkedList<>();
        for (IRStmt statement : seq.stmts()) {
            functionBody.addAll(TileContainer.matchStatement(statement));
        }

        // generateFunctionPrologue needs to be called after the body has been
        // generated to know the number of spilled temps that need to be allocated

        lines.addAll(generateFunctionPrologue());
        lines.addAll(RegisterAllocator.translate(functionBody));
        lines.addAll(generateFunctionEpilogue());
    }

    private static List<AssemblyLine> generateFunctionPrologue() {
        List<AssemblyLine> lines = new LinkedList<>();
        lines.add(new AssemblyComment("Starting function prologue"));

        /*
                Return Address          (8 byte)
         rbp--> Saved base pointer      (8 byte)
                Callee-saved registers  (6 * 8 bytes)
                Caller-saved registers  (10 * 8 bytes)
                Func extra Return Space (n * 8 bytes, goes in Rn ... R3 by decreasing address)
                Scratch space for regs  (this.numScratchRegisters * 8 bytes)
                Space for temps         (variable number)
                Empty alignment         (optional)
                Func extra Arg Space    (n * 8 bytes, goes in An ... A6 by decreasing address)
         */

        // Represents the "offset" of the base point (rbp - stackFrameSize).
        int stackFrameSize = Configuration.WORD_SIZE;

        // Allocate space for callee-save registers rbx rbp r12-r15
        stackFrameSize += Configuration.WORD_SIZE * AssemblyPhysicalRegister.calleeSavedRegisters.length;

        // Allocate space for caller-save registers rax, rcx, rsi, rdi, rdx, rsp, r8, r9, r10, r11
        stackFrameSize += Configuration.WORD_SIZE * AssemblyPhysicalRegister.callerSavedRegisters.length;

        // Make space for return values
        stackFrameSize += Configuration.WORD_SIZE * maxNumReturnValues;

        // Make space for scratch registers
        stackFrameSize += Configuration.WORD_SIZE * numScratchRegisters; // RAX, RDX;

        // Make space for temps
        stackFrameSize += Configuration.WORD_SIZE * AssemblyAbstractRegister.counter;

        // Make sure stack frame is 16 byte aligned
        if (stackFrameSize % 16 != 0) {
            padding = Configuration.WORD_SIZE;
            stackFrameSize += padding;
        }

        // Make space for arguments
        stackFrameSize += Configuration.WORD_SIZE * maxNumArguments;

        // ENTER stackFrameSize 0 is equivalent to
        // push rbp
        // mov rsp, rbp
        // sub stackFrameSize rsp
        lines.add(
                new AssemblyInstruction(
                        OpCode.ENTER,
                        new AssemblyImmediate(stackFrameSize),
                        new AssemblyImmediate(0)
                )
        );

        // Save callee-save registers rbx rbp r12-r15
        lines.add(new AssemblyComment("Save callee-save registers rbx rbp r12-r15"));
        for (int i = 0; i < AssemblyPhysicalRegister.calleeSavedRegisters.length; i++) {
            lines.add(
                    new AssemblyInstruction(
                            OpCode.MOVQ,
                            AssemblyPhysicalRegister.calleeSavedRegisters[i],
                            AssemblyMemoryLocation.stackOffset(
                                    getCalleeSpaceOffset() + Configuration.WORD_SIZE * i)
                    )
            );
        }

        return lines;
    }

    private static List<AssemblyLine> generateFunctionEpilogue() {
        List<AssemblyLine> lines = new LinkedList<>();
        lines.add(new AssemblyComment("Function epilogue"));

        // Restore callee-save registers
        lines.add(new AssemblyComment("Restoring callee-save registers"));
        for (int i = 0; i < AssemblyPhysicalRegister.calleeSavedRegisters.length; i++) {
            AssemblyPhysicalRegister register = AssemblyPhysicalRegister.calleeSavedRegisters[i];
            lines.add(new AssemblyInstruction(
                    OpCode.MOVQ,
                    AssemblyMemoryLocation.stackOffset(Configuration.WORD_SIZE * (1 + i)),
                    register
            ));
        }
        // Restore old RBP and RSP
        lines.add(new AssemblyComment("Restore old RBP and RSP"));
        // Put %rsp back to where the instruction pointer is
        lines.add(new AssemblyInstruction(OpCode.LEAVE));
        lines.add(new AssemblyInstruction(OpCode.RETQ));
        return lines;
    }

    /**
     * Returns the highest memory address in the caller save register space
     */
    public static int getCalleeSpaceOffset() {
        return Configuration.WORD_SIZE;
    }

    /**
     * Returns the highest memory address in the caller save register space
     */
    public static int getCallerSpaceOffset() {
        return getCalleeSpaceOffset() + Configuration.WORD_SIZE * AssemblyPhysicalRegister.calleeSavedRegisters.length;
    }

    /**
     * Returns lowest memory address in the return value space
     */
    public static int getReturnValuesOffset() {
        return getCallerSpaceOffset() +
                Configuration.WORD_SIZE * AssemblyPhysicalRegister.callerSavedRegisters.length +
                Configuration.WORD_SIZE * (maxNumReturnValues - 1);
    }

    /**
     * Returns the highest memory address in the scratch space
     */
    public static int getScratchSpaceOffset() {
        return getReturnValuesOffset() + Configuration.WORD_SIZE;
    }

    /**
     * Returns the highest memory address in the spilled temp space
     */
    public static int getTempSpaceOffset() {
        return getScratchSpaceOffset() + Configuration.WORD_SIZE * numScratchRegisters;
    }

    /**
     * Returns lowest memory address in the argument space
     */
    public static int getArgumentsOffset() {
        return getTempSpaceOffset() + Configuration.WORD_SIZE * AssemblyAbstractRegister.counter +
                Configuration.WORD_SIZE * (maxNumArguments - 1);
    }

    @Override
    public String toString() {
        String s = "";
        s += "\t\t.globl\t" + name + "\n";
        for(String globalName : global) {
            s+= "\t\t.globl\t" + globalName + "\n";
        }
        s += "\t\t.align\t4\n";
        s += name + ":\n";
        for (AssemblyLine line : lines) {
            if (line instanceof AssemblyLabel) {
                s += line + "\n";
            } else {
                s += "\t\t" + line + "\n";
            }
        }
        return s;
    }
}
