package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;


import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.*;

public class AbstractAssemblyGenerator {
    public static int maxNumReturnValues;
    public static int maxNumArguments;
    public static final int numScratchRegisters = 2;

    /**
     * Generate abstract assembly code for a program
     * @param root IRCompUnit of program
     * @return
     */
    public static Map<String, List<AssemblyInstruction>> generate(IRCompUnit root) {
        // Get the maximum number of return values and arguments in all functions
        for (String functionName : root.functions().keySet()) {
            maxNumReturnValues = Math.max(maxNumReturnValues, numReturnValues(functionName));
            maxNumArguments = Math.max(maxNumArguments, numArguments(functionName));
        }

        Map<String, List<AssemblyInstruction>> functions = new LinkedHashMap<>();
        for (String functionName : root.functions().keySet()) {
            functions.put(functionName, generateFunction(root.functions().get(functionName)));
        }
        return functions;
    }

    /**
     * Returns the number of return values for a given function
     */
    private static int numArguments(String functionName) {
        int numArguments = 0;
        int lastUnderscore = functionName.lastIndexOf('_');
        String types = functionName.substring(lastUnderscore + 1);
        int i = 0;

        if (types.charAt(0) == 'p') {
            // example: main(args: int[][]) -> _Imain_paai
            i = 1;

        } else if (types.charAt(0) == 't') {
            // example: parseInt(str: int[]): int, bool -> _IparseInt_t2ibai
            i = 1;
            int numReturnValues = 0;
            while (Character.isDigit(types.charAt(i))) {
                // think carefully
                numReturnValues *= 10;
                numReturnValues += Integer.parseInt("" + types.charAt(i));
            }

            // preemptively compensate for the # of return values
            numArguments -= numReturnValues;

        } else {
            // example: unparseInt(n: int): int[] -> _IunparseInt_aii

            // see above
            numArguments--;
        }

        // adds up number of arguments and number of return values
        while (i < types.length()) {
            while (i < types.length() && types.charAt(i) == 'a') {
                i++;
            }
            numArguments++;
            i++;
        }

        return numArguments;
    }

    /**
     * Returns the number of return values for a given function
     */
    private static int numReturnValues(String functionName) {
        int numReturnValues;
        int lastUnderscore = functionName.lastIndexOf('_');
        String returnTypes = functionName.substring(lastUnderscore + 1);

        if (returnTypes.contains("p")) {
            // example: main(args: int[][]) -> _Imain_paai
            numReturnValues = 0;

        } else if (!returnTypes.contains("t")) {
            // example: unparseInt(n: int): int[] -> _IunparseInt_aii
            numReturnValues = 1;

        } else {
            // example: parseInt(str: int[]): int, bool -> _IparseInt_t2ibai
            int i = 1;
            while (Character.isDigit(returnTypes.charAt(i))) {
                i++;
            }
            numReturnValues = Integer.parseInt(returnTypes.substring(1, i));
        }
        return numReturnValues;
    }

    /**
     * Generate abstract assembly code for a single function
     * @param func the IRFuncDecl to be translated to abstract assembly
     * @return
     */
    private static List<AssemblyInstruction> generateFunction(IRFuncDecl func) {
        // Reset the space for abstract registers
        AssemblyAbstractRegister.reset();

        // Generate assembly instructions for this function
        IRSeq seq = (IRSeq) func.body();
        List<AssemblyInstruction> instructions = new LinkedList<>();
        for (IRStmt statement : seq.stmts()) {
            instructions.addAll(TileContainer.matchStatement(statement));
        }
        return instructions;
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
                Space for temps (variable number)
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
}
