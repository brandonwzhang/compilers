package com.bwz6jk2227esl89ahj34.code_generation;

import java.util.List;

public class AssemblyPhysicalRegister extends AssemblyRegister {

    public static final AssemblyPhysicalRegister RDX = new AssemblyPhysicalRegister(Register.RDX);
    public static final AssemblyPhysicalRegister AL = new AssemblyPhysicalRegister(Register.AL);

    // caller saved
    public static final AssemblyPhysicalRegister RAX = new AssemblyPhysicalRegister(Register.RAX);
    public static final AssemblyPhysicalRegister RCX = new AssemblyPhysicalRegister(Register.RCX);
    public static final AssemblyPhysicalRegister RSI = new AssemblyPhysicalRegister(Register.RSI);
    public static final AssemblyPhysicalRegister RDI = new AssemblyPhysicalRegister(Register.RDI);
    public static final AssemblyPhysicalRegister RSP = new AssemblyPhysicalRegister(Register.RSP);
    public static final AssemblyPhysicalRegister R8 = new AssemblyPhysicalRegister(Register.R8);
    public static final AssemblyPhysicalRegister R9 = new AssemblyPhysicalRegister(Register.R9);
    public static final AssemblyPhysicalRegister R10 = new AssemblyPhysicalRegister(Register.R10);
    public static final AssemblyPhysicalRegister R11 = new AssemblyPhysicalRegister(Register.R11);

    public static final AssemblyPhysicalRegister[] callerSavedRegisters = {
            RAX, RCX, RSI, RDI, RSP, R8, R9, R10, R11
    };

    public enum Register {
        RAX, RBX, RCX, RDX, RBP, RSI, RDI, RSP,
        R8, R9, R10, R11, R12, R13, R14, R15,
        AL
    }

    private Register register;

    public AssemblyPhysicalRegister(Register r) {
        register = r;
    }

    @Override
    public String toString() {
        return "%" + register;
    }

    /*
        Saves the registers by pushing to the stack in order provided.
    */
    public static void saveToStack(List<AssemblyInstruction> instructions, AssemblyPhysicalRegister... registers){
        for (AssemblyPhysicalRegister register : registers) {
            instructions.add(new AssemblyInstruction(AssemblyInstruction.OpCode.PUSHQ, register));
        }
    }

    /*
        Restores the registers by popping from the stack in the order provided (should be reversed from above).
    */
    public static void restoreFromStack(List<AssemblyInstruction> instructions, AssemblyPhysicalRegister... registers){
        for (AssemblyPhysicalRegister register : registers) {
            instructions.add(new AssemblyInstruction(AssemblyInstruction.OpCode.POPQ, register));
        }
    }
}
