package com.bwz6jk2227esl89ahj34.assembly;

import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyPhysicalRegister extends AssemblyRegister {

    // lowest 8 bits of RAX
    public static final AssemblyPhysicalRegister AL = new AssemblyPhysicalRegister(Register.AL);

    // caller saved
    public static final AssemblyPhysicalRegister RAX = new AssemblyPhysicalRegister(Register.RAX);
    public static final AssemblyPhysicalRegister RCX = new AssemblyPhysicalRegister(Register.RCX);
    public static final AssemblyPhysicalRegister RSI = new AssemblyPhysicalRegister(Register.RSI);
    public static final AssemblyPhysicalRegister RDI = new AssemblyPhysicalRegister(Register.RDI);
    public static final AssemblyPhysicalRegister RDX = new AssemblyPhysicalRegister(Register.RDX);
    public static final AssemblyPhysicalRegister RSP = new AssemblyPhysicalRegister(Register.RSP);
    public static final AssemblyPhysicalRegister R8 = new AssemblyPhysicalRegister(Register.R8);
    public static final AssemblyPhysicalRegister R9 = new AssemblyPhysicalRegister(Register.R9);
    public static final AssemblyPhysicalRegister R10 = new AssemblyPhysicalRegister(Register.R10);
    public static final AssemblyPhysicalRegister R11 = new AssemblyPhysicalRegister(Register.R11);

    // callee saved
    public static final AssemblyPhysicalRegister RBP = new AssemblyPhysicalRegister(Register.RBP);
    public static final AssemblyPhysicalRegister RBX = new AssemblyPhysicalRegister(Register.RBX);
    public static final AssemblyPhysicalRegister R12 = new AssemblyPhysicalRegister(Register.R12);
    public static final AssemblyPhysicalRegister R13 = new AssemblyPhysicalRegister(Register.R13);
    public static final AssemblyPhysicalRegister R14 = new AssemblyPhysicalRegister(Register.R14);
    public static final AssemblyPhysicalRegister R15 = new AssemblyPhysicalRegister(Register.R15);

    // neither
    public static final AssemblyPhysicalRegister RIP = new AssemblyPhysicalRegister(Register.RIP);

    // We take out RAX and RDX since we don't want to overwrite return values
    public static final AssemblyPhysicalRegister[] callerSavedRegisters = {
            RCX, RSI, RDI, RSP, R8, R9, R10, R11
    };

    public static final AssemblyPhysicalRegister[] calleeSavedRegisters = {
            RBX, RBP, R12, R13, R14, R15
    };

    public static final AssemblyPhysicalRegister[] argumentRegisters = {
            RDI, RSI, RDX, RCX, R8
    };

    public static final AssemblyPhysicalRegister[] returnRegisters = {
            RAX, RDX
    };

    public static final AssemblyPhysicalRegister[] shuttleRegisters = {
            R14, R15, RBX
    };

    public static final AssemblyPhysicalRegister[] availableRegisters = {
            R10, R11, R12, R13
    };

    public enum Register {
        RAX, RBX, RCX, RDX, RBP, RSI, RDI, RSP,
        R8, R9, R10, R11, R12, R13, R14, R15,
        AL, RIP
    }

    private Register register;

    public AssemblyPhysicalRegister(Register r) {
        register = r;
    }

    @Override
    public String toString() {
        return "%" + register.toString().toLowerCase();
    }

    /*
        Saves the registers by pushing to the stack in order provided. NOTE: puts them into scratch register space.
    */
    public static void saveToStack(List<AssemblyLine> instructions, int offset, AssemblyPhysicalRegister... registers){
        for (int i = 0; i < registers.length; i++) {
            AssemblyPhysicalRegister register = registers[i];
            instructions.add(new AssemblyInstruction(
                    AssemblyInstruction.OpCode.MOVQ,
                    register,
                    AssemblyMemoryLocation.stackOffset(offset + (Configuration.WORD_SIZE * i))));
        }
    }

    /*
        Restores the registers by popping from the stack in the reverse order provided (should be same as above).
    */
    public static void restoreFromStack(List<AssemblyLine> instructions, int offset, AssemblyPhysicalRegister... registers){
        for (int i = 0; i < registers.length; i++) {
            AssemblyPhysicalRegister register = registers[i];
            instructions.add(new AssemblyInstruction(
                    AssemblyInstruction.OpCode.MOVQ,
                    AssemblyMemoryLocation.stackOffset(offset + (Configuration.WORD_SIZE * i)),
                    register));
        }
    }
}
