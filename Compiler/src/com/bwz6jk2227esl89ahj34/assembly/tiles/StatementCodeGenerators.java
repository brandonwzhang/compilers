package com.bwz6jk2227esl89ahj34.assembly.tiles;

import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.LinkedList;
import java.util.List;

public class StatementCodeGenerators {

    public StatementCodeGenerators() {

    }

    private static void addAssemblyComment(IRNode root,
                                           String name,
                                           List<AssemblyLine> lines) {
        String irStr = "" + root;
        lines.add(new AssemblyComment(irStr.substring(0, irStr.length() - 1) + " -- " + name));
    }

    public static StatementTile.CodeGenerator move1 = (root) -> {
        /* MOVE(dst, src) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "move1", lines);

        IRMove castedRoot = (IRMove) root;

        AssemblyExpression src = translateExpression(castedRoot.expr(), lines, true);
        AssemblyExpression dst = translateExpression(castedRoot.target(), lines, false);

        assert !(dst instanceof AssemblyImmediate);
        lines.add(new AssemblyInstruction(OpCode.MOVQ, src, dst));

        return lines;
    };

    public static StatementTile.CodeGenerator move2 = (root) -> {
        /* MOVE(TEMP, CALL(NAME)) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "move2", lines);

        IRMove castedRoot = (IRMove) root;

        // prologue, call, move, epilogue
        functionCall(castedRoot.expr(), lines);
        functionCallEpilogue(lines);
        lines.add(
                new AssemblyInstruction(OpCode.MOVQ,
                        AssemblyPhysicalRegister.RAX,
                        translateExpression(castedRoot.target(), lines, false))
        );

        return lines;
    };

    public static StatementTile.CodeGenerator jump1 = (root) -> {
        /* JUMP(label) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "jump1", lines);

        IRJump castedRoot = (IRJump) root;
        AssemblyExpression label = translateExpression(castedRoot.target(), lines, true);

        assert label instanceof AssemblyName;
        lines.add(new AssemblyInstruction(OpCode.JMP, label));

        return lines;
    };

    public static StatementTile.CodeGenerator label1 = (root) -> {
        /* LABEL(name) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "label1", lines);

        IRLabel castedRoot = (IRLabel) root;
        AssemblyLabel label = new AssemblyLabel(new AssemblyName(castedRoot.name()));

        lines.add(label);
        return lines;
    };

    public static StatementTile.CodeGenerator exp1 = (root) -> {
      	/* EXP(CALL(NAME)) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "exp1", lines);

        IRExp castedRoot = (IRExp) root;

        // prologue, call, epilogue
        functionCall(castedRoot.expr(), lines);
        functionCallEpilogue(lines);
        return lines;
    };

    /**
     * For convenience, we take care of the "function epilogue" in AssemblyFunction
     * In the IR, we guarantee that there is only a single return at the end of a function
     */
    public static StatementTile.CodeGenerator return1 = (root) -> {
        /* RETURN() */
        // Handled in AssemblyFunction.generateFunctionEpilogue
        return AssemblyFunction.generateFunctionEpilogue();
    };

    public static StatementTile.CodeGenerator cjump1 = (root) -> {
        /* CJUMP(e, trueLabel) */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "cjump1", lines);

        IRCJump castedRoot = (IRCJump) root;
        assert castedRoot.falseLabel() == null; // assert lowered CJump
        AssemblyExpression guard = translateExpression(castedRoot.expr(), lines, true);

        // compare guard to 0, jump to trueLabel if not equal
        lines.add(new AssemblyInstruction(OpCode.CMPQ, new AssemblyImmediate(0), guard));
        lines.add(new AssemblyInstruction(OpCode.JNE, new AssemblyName(castedRoot.trueLabel())));

        return lines;
    };

    public static StatementTile.CodeGenerator move3456 = (root) -> {
        /*
                           MOVE
                  TEMP(t1)                ADD
                                   TEMP(t1)     MEM
                                                ADD
                                           TEMP(fp) 4
                  allow the additions to be commutative
         */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        lines.add(new AssemblyComment("nontrivial tile move3456"));

        IRMove castedRoot = (IRMove) root;
        assert castedRoot.target() instanceof IRTemp;
        IRTemp dest = (IRTemp) castedRoot.target();

        assert castedRoot.expr() instanceof IRBinOp;
        IRBinOp castedBinOp = (IRBinOp) castedRoot.expr();
        IRMem mem = (castedBinOp.left() instanceof IRMem)
                ? (IRMem) castedBinOp.left() : (IRMem) castedBinOp.right();

        assert mem.expr() instanceof IRBinOp;
        IRBinOp castedMemExpr = (IRBinOp)mem.expr();

        IRConst offset;
        IRTemp temp;
        if (castedMemExpr.left() instanceof IRConst) {
            offset = (IRConst) castedMemExpr.left();
            temp = (IRTemp) castedMemExpr.right();
        } else {
            offset = (IRConst) castedMemExpr.right();
            temp =  (IRTemp) castedMemExpr.left();
        }

        AssemblyMemoryLocation memLocation =
                new AssemblyMemoryLocation(
                        new AssemblyAbstractRegister(temp),
                        null,
                        offset.value()
                );

        lines.add(new AssemblyInstruction(
                OpCode.ADDQ,
                memLocation,
                new AssemblyAbstractRegister(dest)
        ));

        return lines;
    };

    public static StatementTile.CodeGenerator move78910 = (root) ->{
        /*
                           MOVE
                  TEMP(t1)                ADD/SUB
                                   TEMP(t1)     CONST/TEMP
                  allow the add/sub to be commutative
         */

        LinkedList<AssemblyLine> lines = new LinkedList<>();
        lines.add(new AssemblyComment("nontrivial tile move3456"));
        IRMove castedRoot = (IRMove) root;
        IRTemp t = (IRTemp) (castedRoot.target());
        IRBinOp binOp = (IRBinOp) (castedRoot.expr());
        IRExpr expr = binOp.left() instanceof IRTemp ? binOp.right() : binOp.left();
        OpCode opcode = binOp.opType() == OpType.ADD ? OpCode.ADDQ : OpCode.SUBQ;
        if (expr instanceof IRTemp) {
            lines.add(new AssemblyInstruction(
                    opcode,
                    new AssemblyAbstractRegister((IRTemp)expr),
                    new AssemblyAbstractRegister(t)
                    )
            );
        } else {
            lines.add(new AssemblyInstruction(
                    opcode,
                    new AssemblyImmediate(((IRConst)expr).value()),
                    new AssemblyAbstractRegister(t)
              )
            );
        }
        return lines;
    };

    /**
     * Translate an IRExpr in to its corresponding abstract assembly
     * representation
     * If the IRExpr is an argument or return temp, it will translate directly
     * to a physical location
     * All moves should use this function to translate
     * @param expr the IRExpr to be translated
     * @param lines the List<AssemblyLine> to add instructions to
     * @param isUsed a boolean indicating whether this IRExpr is being used as opposed to being set
     */
    private static AssemblyExpression translateExpression(IRExpr expr,
                                                          List<AssemblyLine> lines,
                                                          boolean isUsed) {
        // If it's not an IRTemp, we just need to match it normally
        if (!(expr instanceof IRTemp)) {
            return TileContainer.matchExpression(expr, lines);
        }

        // If we have a temp, we need to determine if it's an argument temp,
        // a return temp, or neither

        // If it's an argument temp, the expression we translate to depends on
        // whether the temp is being used or being set
        // If it's being used, we need to access it from our parent's stack frame
        // If it's being set, we just put it in our stack frame in the argument space
        IRTemp temp = (IRTemp) expr;
        int argTempNumber = getArgumentTempNumber(temp);
        if (argTempNumber >= 0) {
            // We have an argument temp
            AssemblyMemoryLocation argLocation;
            if (isUsed) {
                // Get the argument from the parent's stack frame
                argLocation = new AssemblyMemoryLocation(AssemblyPhysicalRegister.RBP, Configuration.WORD_SIZE * 2);
            } else {
                // Move the argument to the argument space in our stack frame for
                // and functions we call to access
                argLocation = AssemblyMemoryLocation.stackOffset(AssemblyFunction.getArgumentsOffset());
            }
            return getArgumentMapping(argTempNumber, argLocation);
        }

        // If it's a return temp, the expression we translate to depends on
        // whether the temp is being used or set
        // If it's being used, we just need to take it from the return space of
        // our stack frame
        int returnTempNumber = getReturnTempNumber(temp);
        if (returnTempNumber >= 0) {
            // We have a return temp
            AssemblyMemoryLocation retLocation;
            if (isUsed) {
                // Get the return value from our stack frame that another function returned
                retLocation = AssemblyMemoryLocation.stackOffset(AssemblyFunction.getReturnValuesOffset());
            } else {
                // Move the return value to the parent's stack frame so it can access it
                // We were passed the pointer to the return space in the parent stack frame
                retLocation = new AssemblyMemoryLocation(AssemblyPhysicalRegister.R9);
            }
            return getReturnMapping(returnTempNumber, retLocation);
        }

        // We don't have an argument or return temp, so we just translate it normally
        return TileContainer.matchExpression(temp, lines);
    }

    /**
     * Returns the number of the return temp. Returns -1 if not a return temp.
     */
    private static int getArgumentTempNumber(IRTemp temp) {
        String name = temp.name();
        if (name.length() < 5) {
            return -1;
        }
        if (name.substring(0, 4).equals(Configuration.ABSTRACT_ARG_PREFIX)) {
            return Integer.parseInt(name.substring(4));
        }
        return -1;
    }

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

    /**
     * Returns the argument register or memory location that the argument temp corresponds to
     */
    private static AssemblyExpression getArgumentMapping(int id, AssemblyMemoryLocation argumentsSpace) {
        // If the id is lower than the number of argument registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.argumentRegisters.length) {
            return AssemblyPhysicalRegister.argumentRegisters[id];
        }
        // Return the corresponding memory location
        argumentsSpace.displacement +=
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.argumentRegisters.length);
        return argumentsSpace;
    }

    /**
     * Returns the return register or memory location that the return temp corresponds to
     */
    private static AssemblyExpression getReturnMapping(int id, AssemblyMemoryLocation returnValuesSpace) {
        // If the id is lower than the number of return registers available,
        // return the corresponding register
        if (id < AssemblyPhysicalRegister.returnRegisters.length) {
            return AssemblyPhysicalRegister.returnRegisters[id];
        }
        // Return the corresponding memory location
        returnValuesSpace.displacement +=
                Configuration.WORD_SIZE * (id - AssemblyPhysicalRegister.returnRegisters.length);
        return returnValuesSpace;
    }

    /**
     * Adds the function prologue and the call itself
     * Does not handle the function epilogue
     */
    private static void functionCall(IRNode node, List<AssemblyLine> lines) {
        assert node instanceof IRCall;
        IRCall castedNode = (IRCall) node;

        /* Function Call Prologue */
        lines.add(new AssemblyComment("Function call prologue"));
        // Save all caller-saved registers
        lines.add(new AssemblyComment("Save all caller-saved registers"));
        AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

        // Pass pointer to return space as first argument (R9)
        lines.add(new AssemblyComment("Pass pointer to return space"));
        lines.add(new AssemblyInstruction(
                OpCode.LEAQ,
                AssemblyMemoryLocation.stackOffset(AssemblyFunction.getReturnValuesOffset()),
                AssemblyPhysicalRegister.R9
        ));

        // Put arguments in order rdi rsi rdx rcx
        List<IRExpr> arguments = castedNode.args();

        // Move the return values to the return value space in the parent's stack
        lines.add(new AssemblyComment("Passing in arguments..."));
        int numArgs = AssemblyPhysicalRegister.argumentRegisters.length;
        for(int i = 0; i < arguments.size(); i++) {
            if (i < numArgs) {
                lines.add(
                        new AssemblyInstruction(
                                OpCode.MOVQ,
                                translateExpression(arguments.get(i), lines, true),
                                AssemblyPhysicalRegister.argumentRegisters[i]
                        )
                );
            } else { // put into stack location
                lines.add(
                        new AssemblyInstruction(
                                OpCode.MOVQ,
                                translateExpression(arguments.get(i), lines, true),
                                new AssemblyMemoryLocation(AssemblyPhysicalRegister.RSP,
                                        Configuration.WORD_SIZE * (i - numArgs)))
                );
            }
        }

         // Get function name
        AssemblyExpression name = translateExpression(castedNode.target(), lines, true);
        assert name instanceof AssemblyName;

        // Add the call instruction to lines
        lines.add(
                new AssemblyInstruction(
                        OpCode.CALLQ,
                        name
                )
        );
    }

    /**
     * Restores the caller-save registers
     */
    private static void functionCallEpilogue(List<AssemblyLine> lines) {
        lines.add(new AssemblyComment("Function call epilogue"));
        // Now we restore all of the caller saved registers
        AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

    }
}
