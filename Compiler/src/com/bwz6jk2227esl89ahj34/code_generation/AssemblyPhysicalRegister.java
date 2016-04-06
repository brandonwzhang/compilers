package com.bwz6jk2227esl89ahj34.code_generation;

public class AssemblyPhysicalRegister extends AssemblyRegister {
    public enum Register {
        RAX, RBX, RCX, RDX, RBP, RSI, RDI, RSP,
        R8, R9, R10, R11, R12, R13, R14, R15;
    }

    private Register register;

    public AssemblyPhysicalRegister(Register r) {
        register = r;
    }

    @Override
    public String toString() {
        return "%" + register;
    }
}
