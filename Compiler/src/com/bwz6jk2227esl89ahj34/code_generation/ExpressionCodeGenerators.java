package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import java.util.List;

public class ExpressionCodeGenerators {
    private static TileContainer tileContainer = AbstractAssemblyGenerator.tileContainer;

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

    public static ExpressionTile.CodeGenerator mem1 = (root, instructions) -> {
            /*
                MEM(e)
             */
        IRMem castedRoot = (IRMem) root;
        AssemblyExpression e = tileContainer.matchExpression(castedRoot.expr(), instructions);
        assert e instanceof AssemblyAbstractRegister;

        return new AssemblyMemoryLocation((AssemblyAbstractRegister)e);
    };


    public static ExpressionTile.CodeGenerator name1 = (root, instructions) -> {
    		/*
        		NAME("name")
        */
        return null;
    };

    public static ExpressionTile.CodeGenerator binop1 = (root, instructions) -> {
      	/*
        		Handles all BinOp(e1, e2), for all e1,e2 NOT IRMem
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression e1 = tileContainer.matchExpression(castedRoot.left(), instructions);
        AssemblyExpression e2 = tileContainer.matchExpression(castedRoot.right(), instructions);

        assert !(e1 instanceof AssemblyMemoryLocation);
        assert !(e2 instanceof AssemblyMemoryLocation);

        return binopHelper(castedRoot.opType(), e1, e2, instructions);
    };

    public static ExpressionTile.CodeGenerator binop2 = (root, instructions) -> {
    		/*
        		Handles all BinOp(e, IRMem)
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression e = tileContainer.matchExpression(castedRoot.left(), instructions);
        AssemblyExpression mem = tileContainer.matchExpression(castedRoot.right(), instructions);
        AssemblyAbstractRegister reg = new AssemblyAbstractRegister();
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, mem, reg));
        return binopHelper(castedRoot.opType(), e, reg, instructions);
    };

    public static ExpressionTile.CodeGenerator binop3 = (root, instructions) -> {
    		/*
        		Handles all BinOp(IRMem, e)
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression mem = tileContainer.matchExpression(castedRoot.left(), instructions);
        AssemblyAbstractRegister reg = new AssemblyAbstractRegister();
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, mem, reg));
        AssemblyExpression e = tileContainer.matchExpression(castedRoot.right(), instructions);
        return binopHelper(castedRoot.opType(), reg, e, instructions);
    };

    public static ExpressionTile.CodeGenerator binop4 = (root, instructions) -> {
    		/*
        		Handles all BinOp(IRMem, IRMem)
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression mem1 = tileContainer.matchExpression(castedRoot.left(), instructions);
        AssemblyAbstractRegister reg1 = new AssemblyAbstractRegister();
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, mem1, reg1));
        AssemblyExpression mem2 = tileContainer.matchExpression(castedRoot.right(), instructions);
        AssemblyAbstractRegister reg2 = new AssemblyAbstractRegister();
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, mem2, reg2));
        return binopHelper(castedRoot.opType(), reg1, reg2, instructions);
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
                // movq t1, RAX
                // mulq t2
                // movq RAX, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, new AssemblyPhysicalRegister(Register.RAX)));
                instructions.add(new AssemblyInstruction(OpCode.MULQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyPhysicalRegister(Register.RAX), t));
                return t;
            case HMUL:
                // make new t
                // movq t1, RAX
                // mulq t2
                // movq RDX, t
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, new AssemblyPhysicalRegister(Register.RAX)));
                instructions.add(new AssemblyInstruction(OpCode.MULQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyPhysicalRegister(Register.RDX), t));
                return t;
            case DIV:
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RAX, t #RAX contains quotient
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), new AssemblyPhysicalRegister(Register.RDX)));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, new AssemblyPhysicalRegister(Register.RAX)));
                instructions.add(new AssemblyInstruction(OpCode.DIVQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyPhysicalRegister(Register.RAX), t));
                return t;
            case MOD:
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RDX, t #RDX contains remainder
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), new AssemblyPhysicalRegister(Register.RDX)));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, left, new AssemblyPhysicalRegister(Register.RAX)));
                instructions.add(new AssemblyInstruction(OpCode.DIVQ, right));
                instructions.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyPhysicalRegister(Register.RDX), t));
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
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETZQ, t));
                return t;
            case NEQ:
                // cmp t1, t2
                // setnzq t #sets t to 0 if zero flag
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETNZQ, t));
                return t;
            case LT:
                // cmp t1, t2
                // setlq t #sets t to 1 if sign flag != overflow flag
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETGQ, t));
                return t;
            case GT:
                // cmp t1, t2
                // setgq t #sets t to 1 if zero flag = 0 or sign flag = overflow flag
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETLQ, t));
                return t;
            case LEQ:
                // cmp t1, t2
                // setleq t #sets t to 1 if zero flag = 1 or sign flag != overflow flag
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETLEQ, t));
                return t;
            case GEQ:
                // cmp t1, t2
                // setgeq t #sets t to 1 if
                instructions.add(new AssemblyInstruction(OpCode.CMP, left, right));
                instructions.add(new AssemblyInstruction(OpCode.SETGEQ, t));
                return t;
            default:
                throw new RuntimeException("Please contact andru@cs.cornell.edu");

        }

    }
}
