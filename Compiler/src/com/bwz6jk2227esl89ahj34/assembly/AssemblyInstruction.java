package com.bwz6jk2227esl89ahj34.assembly;

import com.bwz6jk2227esl89ahj34.ir.IRName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyInstruction extends AssemblyLine {
    public enum OpCode {
        ADDQ, SUBQ, ANDQ, CMPQ, DIVQ ,ORQ, MOVQ, MULQ, SETZ, SETNZ, SETL, SETG, SETLE,
        SETGE, PUSHQ, POPQ, JMP, XORQ, RETQ, JE, JNE, MOVZX, CALLQ, ENTER, LEAVE, LEAQ,
        IMULQ, IDIVQ
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
        // special case for method dispatching
        if (opCode.equals(OpCode.CALLQ) && !(args.get(0) instanceof AssemblyName)) {
            return "callq\t" + "*" + args.get(0);
        }

        String s = opCode.toString().toLowerCase() + "\t";
        for (int i = 0; i < args.size(); i++) {
            s += args.get(i);
            if (i < args.size() - 1) {
                s += ", ";
            }
        }
        return s;
    }
}
