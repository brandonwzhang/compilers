package com.bwz6jk2227esl89ahj34.code_generation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
public class AssemblyInstruction extends AssemblyLine {
    public enum OpCode {
        ADDQ, SUBQ, ANDQ, CMP, DIVQ ,ORQ, MOVQ, MULQ, SETZQ, SETNZQ, SETLQ, SETGQ, SETLEQ,
        SETGEQ, PUSHQ, POPQ, JMP, XORQ, RETQ, JE, JNE, MOVZX, CALLQ
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
        String s = opCode.toString().toLowerCase() + "\t";
        if (opCode == OpCode.CALLQ) {
            s += " FUNC(" + args.get(0) + ")";
            return s;
        }
        for (int i = 0; i < args.size(); i++) {
            s += args.get(i);
            if (i < args.size() - 1) {
                s += ",";
            }
        }
        return s;
    }
}
