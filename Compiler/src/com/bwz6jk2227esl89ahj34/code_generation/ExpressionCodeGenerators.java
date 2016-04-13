package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

import java.util.List;

public class ExpressionCodeGenerators {
    public static ExpressionTile.CodeGenerator const1 = (root, lines) -> {
            /*
                CONST(i)
             */
        IRConst castedRoot = (IRConst) root;
        return new AssemblyImmediate(castedRoot.value());
    };

    public static ExpressionTile.CodeGenerator temp1 = (root, lines) -> {
            /*
                TEMP(t)
             */
        IRTemp castedRoot = (IRTemp) root;
        return new AssemblyAbstractRegister(castedRoot);
    };

    public static ExpressionTile.CodeGenerator mem1 = (root, lines) -> {
            /*
                MEM(e)
             */
        IRMem castedRoot = (IRMem) root;
        AssemblyExpression e = TileContainer.matchExpression(castedRoot.expr(), lines);
        assert e instanceof AssemblyAbstractRegister;

        return new AssemblyMemoryLocation((AssemblyAbstractRegister)e);
    };


    public static ExpressionTile.CodeGenerator name1 = (root, lines) -> {
        /*
        	NAME("name")
        */
        IRName castedRoot = (IRName) root;

        return new AssemblyName(castedRoot.name());
    };

    public static ExpressionTile.CodeGenerator binop1 = (root, lines) -> {
      	/*
        		Handles all BinOp(e1, e2), for all e1,e2 NOT IRMem
        */
        IRBinOp castedRoot = (IRBinOp) root;
        AssemblyExpression e1 = TileContainer.matchExpression(castedRoot.left(), lines);
        AssemblyExpression e2 = TileContainer.matchExpression(castedRoot.right(), lines);

        return binopHelper(castedRoot.opType(), e1, e2, lines);
    };

    //TODO: test
    public static ExpressionTile.CodeGenerator mem234 = (root, lines) -> {
      /*
         Handles mem2 - mem5, hence the name
            MEM
           ADD/SUB
         CONST  TEMP   or vice versa
       */
        IRMem castedRoot = (IRMem) root;
        IRBinOp binop = (IRBinOp)castedRoot.expr();
        AssemblyExpression e1 = TileContainer.matchExpression(binop.left(), lines);
        AssemblyExpression e2 = TileContainer.matchExpression(binop.right(), lines);
        long value = e1 instanceof AssemblyImmediate
                ? ((AssemblyImmediate)e1).getValue()
                : ((AssemblyImmediate)e2).getValue();
        if (binop.opType() == OpType.SUB) {
            value = -1 * value;
        }

        if (e1 instanceof AssemblyImmediate) {
            return new AssemblyMemoryLocation((AssemblyRegister)e2, null,
                    value);
        } else {
            return new AssemblyMemoryLocation((AssemblyRegister)e1, null,
                    value);
        }
    };

    //TODO: test
    public static ExpressionTile.CodeGenerator mem5 = (root, lines) -> {
        /*
          Handles mem6
            MEM
            ADD
         TEMP   TEMP
         */
        IRMem castedRoot = (IRMem) root;
        IRBinOp binop = (IRBinOp) castedRoot.expr();
        AssemblyExpression e1 = TileContainer.matchExpression(binop.left(), lines);
        AssemblyExpression e2 = TileContainer.matchExpression(binop.right(), lines);
        return new AssemblyMemoryLocation((AssemblyRegister) e1, (AssemblyRegister) e2);
    };

    private static AssemblyExpression binopHelper(OpType opType,
                                                  AssemblyExpression left,
                                                  AssemblyExpression right,
                                                  List<AssemblyLine> lines) {
        AssemblyAbstractRegister t = new AssemblyAbstractRegister();
        switch(opType) {
            case ADD:
                // make new t
                // movq left, t
                // addq right, t
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                lines.add(new AssemblyInstruction(OpCode.ADDQ, right, t));
                return t;
            case SUB:
                // make new t
                // movq left, t
                // subq t, right
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                lines.add(new AssemblyInstruction(OpCode.SUBQ, right, t));
                return t;
            case MUL:
                // make new t
                // save whatever in RAX and RDX into stack
                // movq t1, RAX
                // mulq t2
                // movq RAX, t
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                AssemblyAbstractRegister hmulTemp = new AssemblyAbstractRegister();
                lines.add(new AssemblyInstruction(OpCode.MOVQ, right, hmulTemp));
                lines.add(new AssemblyInstruction(OpCode.MULQ, hmulTemp));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RAX, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case HMUL:
                // make new t
                // save RAX, RDX into stack
                // movq t1, RAX
                // mulq t2
                // movq RDX, t
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                AssemblyAbstractRegister mulTemp = new AssemblyAbstractRegister();
                lines.add(new AssemblyInstruction(OpCode.MOVQ, right, mulTemp));
                lines.add(new AssemblyInstruction(OpCode.MULQ, mulTemp));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RDX, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case DIV:
                // save RAX, RDX to stack
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RAX, t #RAX contains quotient
                // restore RDX, RAX
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                lines.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), AssemblyPhysicalRegister.RDX));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                lines.add(new AssemblyInstruction(OpCode.DIVQ, right));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RAX, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case MOD:
                // save RAX, RDX to stack
                // movq $0, RDX
                // movq t1, RAX
                // divq t2 #divides RDX:RAX by t2
                // movq RDX, t #RDX contains remainder
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                lines.add(new AssemblyInstruction(OpCode.MOVQ, new AssemblyImmediate(0), AssemblyPhysicalRegister.RDX));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, AssemblyPhysicalRegister.RAX));
                lines.add(new AssemblyInstruction(OpCode.DIVQ, right));
                lines.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RDX, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RDX);
                return t;
            case AND:
                // movq t1, t
                // andq t2, t
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                lines.add(new AssemblyInstruction(OpCode.ANDQ, right, t));
                return t;
            case OR:
                // movq t1, t
                // orq t2, t
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                lines.add(new AssemblyInstruction(OpCode.ORQ, right, t));
                return t;
            case XOR:
                // movq t1, t
                // xorq t2, t
                lines.add(new AssemblyInstruction(OpCode.MOVQ, left, t));
                lines.add(new AssemblyInstruction(OpCode.XORQ, right, t));
                return t;
            case EQ:
                // cmp t1, t2
                // setzq t #sets t to 1 if zero flag
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETZ, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            case NEQ:
                // cmp t1, t2
                // setnzq t #sets t to 0 if zero flag
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETNZ, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            case LT:
                // cmp t1, t2
                // setlq t #sets t to 1 if sign flag != overflow flag
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETL, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            case GT:
                // cmp t1, t2
                // setgq t #sets t to 1 if zero flag = 0 or sign flag = overflow flag
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETG, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            case LEQ:
                // cmp t1, t2
                // setleq t #sets t to 1 if zero flag = 1 or sign flag != overflow flag
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETLE, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            case GEQ:
                // cmp t1, t2
                // setgeq t #sets t to 1 if
                AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                lines.add(new AssemblyInstruction(OpCode.CMPQ, left, right));
                lines.add(new AssemblyInstruction(OpCode.SETGE, AssemblyPhysicalRegister.AL));
                lines.add(new AssemblyInstruction(OpCode.MOVZX, AssemblyPhysicalRegister.AL, t));
                AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getScratchSpaceOffset(),
                        AssemblyPhysicalRegister.RAX);
                return t;
            default:
                throw new RuntimeException("Please contact andru@cs.cornell.edu");

        }

    }

}
