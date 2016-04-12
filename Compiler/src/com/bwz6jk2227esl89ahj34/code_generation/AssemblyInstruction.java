package com.bwz6jk2227esl89ahj34.code_generation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
public class AssemblyInstruction {
    public enum OpCode {
        ADDQ, SUBQ, ANDQ, CMP, DIVQ ,ORQ, MOVQ, MULQ, SETZQ, SETNZQ, SETLQ, SETGQ, SETLEQ,
        SETGEQ, PUSHQ, POPQ, JMP, XORQ, RETQ, JE, JNE,
        MOVZX;
    }

    public OpCode opCode;
    public List<AssemblyExpression> args;

    public AssemblyInstruction() {

    }

    public AssemblyInstruction(OpCode opCode, AssemblyExpression... args) {
        this.opCode = opCode;
        this.args = Arrays.asList(args);
    }
    @Override
    public String toString() {
        String s = "" + opCode;
        for (AssemblyExpression e : args) {
            s += " " + e;
        }
        return s;
    }
}
