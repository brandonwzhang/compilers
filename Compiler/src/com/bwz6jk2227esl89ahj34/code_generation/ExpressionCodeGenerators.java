package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import java.util.List;

public class ExpressionCodeGenerators {
    public static ExpressionTile.CodeGenerator const1 = (root, instructions) -> {
            /*
                CONST(i)
             */
        IRConst castedRoot = (IRConst) root;
        return new AssemblyImmediate(castedRoot.value());
    };

    public static ExpressionTile.CodeGenerator temp1 = (root, instructions) -> {
            /*
                TEMP(t)
             */
        IRTemp castedRoot = (IRTemp) root;
        return new AssemblyAbstractRegister(castedRoot);
    };

    /**
     * Returns the number of the return temp. Returns -1 if not a return temp.
     */
    private static int getReturnTempNumber(IRTemp temp) {
        String name = temp.name();
        if (name.length() < 5) {
            return -1;
        }
        if (name.substring(0, 4).equals(Configuration.ABSTRACT_RET_PREFIX)) {
            return Integer.parseInt(name.substring(4));
        }
        return -1;
    }

    public static ExpressionTile.CodeGenerator mem1 = (root, instructions) -> {
            /*
                MEM(e)
             */
        IRMem castedRoot = (IRMem) root;
        AssemblyExpression e = AbstractAssemblyGenerator.tileContainer.matchExpression(castedRoot.expr(), instructions);
        assert e instanceof AssemblyAbstractRegister;

        return new AssemblyMemoryLocation((AssemblyAbstractRegister)e);
    };


    public static ExpressionTile.CodeGenerator name1 = (root, instructions) -> {
        /*
        	NAME("name")
        */
        IRName castedRoot = (IRName) root;

        return new AssemblyName(castedRoot.name());
    };

    public static ExpressionTile.CodeGenerator binop1 = (root, instructions) -> {
      	/*
        		Handles all BinOp(e1, e2), for all e1,e2 NOT IRMem
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression e1 = AbstractAssemblyGenerator.tileContainer.matchExpression(castedRoot.left(), instructions);
        AssemblyExpression e2 = AbstractAssemblyGenerator.tileContainer.matchExpression(castedRoot.right(), instructions);

        assert !(e1 instanceof AssemblyMemoryLocation);
        assert !(e2 instanceof AssemblyMemoryLocation);

        return binopHelper(castedRoot.opType(), e1, e2, instructions);
    };

    private static AssemblyExpression binopHelper(OpType opType,
                                                  AssemblyExpression left,
                                                  AssemblyExpression right,
                                                  List<AssemblyInstruction> instructions) {
        AssemblyAbstractRegister t = new AssemblyAbstractRegister();
        switch(opType) {
            case ADD:
                // make new t
                // movq left, t
                // addq right, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                instructions.add(new AssemblyInstruction(OpCode.ADDQ, right, t));
                return t;
            case SUB:
                // make new t
                // movq left, t
                // subq t, right
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                instructions.add(new AssemblyInstruction(OpCode.SUBQ, right, t));
                return t;
            case MUL:
                // make new t
                // save whatever in RAX and RDX into stack
                // movq t1, RAX
                // mulq t2
                // movq RAX, t
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                instructions.add(new AssemblyInstruction(OpCode.MULQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RAX, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case HMUL:
                // make new t
                // save RAX, RDX into stack
                // movq t1, RAX
                // mulq t2
                // movq RDX, t
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                instructions.add(new AssemblyInstruction(OpCode.MULQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RDX, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case DIV:
                // save RAX, RDX to stack
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RAX, t #RAX contains quotient
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), AssemblyPhysicalRegister.RDX));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                instructions.add(new AssemblyInstruction(OpCode.DIVQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RAX, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case MOD:
                // save RAX, RDX to stack
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RDX, t #RDX contains remainder
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), AssemblyPhysicalRegister.RDX));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                instructions.add(new AssemblyInstruction(OpCode.DIVQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RDX, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case AND:
                // movq t1, t
                // andq t2, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                instructions.add(new AssemblyInstruction(OpCode.ANDQ, right, t));
                return t;
            case OR:
                // movq t1, t
                // orq t2, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                instructions.add(new AssemblyInstruction(OpCode.ORQ, right, t));
                return t;
            case XOR:
                // movq t1, t
                // xorq t2, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                instructions.add(new AssemblyInstruction(OpCode.XORQ, right, t));
                return t;
            case EQ:
                // cmp t1, t2
                // setzq t #sets t to 1 if zero flag
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETZQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            case NEQ:
                // cmp t1, t2
                // setnzq t #sets t to 0 if zero flag
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETNZQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            case LT:
                // cmp t1, t2
                // setlq t #sets t to 1 if sign flag != overflow flag
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETLQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            case GT:
                // cmp t1, t2
                // setgq t #sets t to 1 if zero flag = 0 or sign flag = overflow flag
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETGQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            case LEQ:
                // cmp t1, t2
                // setleq t #sets t to 1 if zero flag = 1 or sign flag != overflow flag
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETLEQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            case GEQ:
                // cmp t1, t2
                // setgeq t #sets t to 1 if
                AssemblyPhysicalRegister.saveToStack(instructions, AssemblyPhysicalRegister.RAX);
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETGEQ, AssemblyPhysicalRegister.AL));
                instructions.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(instructions, AssemblyPhysicalRegister.RAX);
                return t;
            default:
                throw new RuntimeException("Please contact andru@cs.cornell.edu");

        }

    }

}
