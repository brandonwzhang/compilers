package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;


import java.util.LinkedList;
import java.util.List;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

public class StatementCodeGenerators {

    public StatementCodeGenerators() {

    }

    private static void addAssemblyComment(IRNode root,
                                           String name,
                                           List<AssemblyLine> lines) {
        String irStr = ("" + root);
        lines.add(new AssemblyComment(""));
        lines.add(new AssemblyComment(irStr.substring(0, irStr.length() - 1)));
        lines.add(new AssemblyComment(name));
    }

    public static StatementTile.CodeGenerator move1 = (root) -> {
        /*
            MOVE(dst, src)
        */
        LinkedList<AssemblyLine> lines = new LinkedList<>();
        addAssemblyComment(root, "move1", lines);

        // Add a comment showing the IRNode that was translated

        IRMove castedRoot = (IRMove) root;
        AssemblyExpression src = TileContainer.matchExpression(castedRoot.expr(), lines);
        AssemblyExpression dst = TileContainer.matchExpression(castedRoot.target(), lines);

        assert !(dst instanceof AssemblyImmediate);
        lines.add(new AssemblyInstruction(OpCode.MOVQ, src, dst));

        return lines;
    };

    public static StatementTile.CodeGenerator jump1 = (root) -> {
        /*
            JUMP(label)
        */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "jump1", lines);

        IRJump castedRoot = (IRJump) root;
        AssemblyExpression label = TileContainer.matchExpression(castedRoot.target(), lines);

        assert label instanceof AssemblyName;
        lines.add(new AssemblyInstruction(OpCode.JMP, label));

        return lines;
    };

    public static StatementTile.CodeGenerator label1 = (root) -> {
        /*
            LABEL(name)
         */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "label1", lines);

        IRLabel castedRoot = (IRLabel) root;
        AssemblyLabel label = new AssemblyLabel(new AssemblyName(castedRoot.name()));

        lines.add(label);
        return lines;
    };

    public static StatementTile.CodeGenerator exp1 = (root) -> {
      	/*
        		EXP(CALL(NAME))
        */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "exp1", lines);

        IRExp castedRoot = (IRExp) root;

        // prologue, call, epilogue
        functionCall(castedRoot.expr(), lines);
        functionCallEpilogue(lines);
        return lines;
    };

    public static StatementTile.CodeGenerator move2 = (root) -> {
    		/*
        		MOVE(TEMP, CALL(NAME))
        */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "move2", lines);

        IRMove castedRoot = (IRMove) root;

        // prologue, call, move, epilogue
        functionCall(castedRoot.expr(), lines);
        lines.add(
                new AssemblyInstruction(OpCode.MOVQ,
                        AssemblyPhysicalRegister.RAX,
                        TileContainer.matchExpression(castedRoot.target(), lines))
        );
        functionCallEpilogue(lines);

        return lines;
    };

    /**
     * For convenience, we take care of the "function epilogue"
     * along with the return statement 
     */
    public static StatementTile.CodeGenerator return1 = (root) -> {
        /*
        		RETURN()
        */
        List<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "return1", lines);

        // Restore callee-save registers
        for (int i = 0; i < AssemblyPhysicalRegister.calleeSavedRegisters.length; i++) {
            AssemblyPhysicalRegister register = AssemblyPhysicalRegister.calleeSavedRegisters[i];
            lines.add(new AssemblyInstruction(
                    OpCode.MOVQ,
                    AssemblyMemoryLocation.stackOffset(Configuration.WORD_SIZE * (1 + i)),
                    register
                    ));
        }
        // Restore old RBP and RSP
        lines.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RBP, AssemblyPhysicalRegister.RSP));
        lines.add(new AssemblyInstruction(OpCode.POPQ, AssemblyPhysicalRegister.RBP));
        lines.add(new AssemblyInstruction(OpCode.RETQ));
        return lines;
    };

    public static StatementTile.CodeGenerator cjump1 = (root) -> {
        /*
            CJUMP(e, trueLabel)
         */
        LinkedList<AssemblyLine> lines = new LinkedList<>();

        // Add a comment showing the IRNode that was translated
        addAssemblyComment(root, "cjump1", lines);

        IRCJump castedRoot = (IRCJump) root;
        assert castedRoot.falseLabel() == null; // assert lowered CJump
        AssemblyExpression guard = TileContainer.matchExpression(castedRoot.expr(), lines);

        // compare guard to 0, jump to trueLabel if not equal
        lines.add(new AssemblyInstruction(OpCode.CMP, guard, new AssemblyImmediate(0)));
        lines.add(new AssemblyInstruction(OpCode.JNE, new AssemblyName(castedRoot.trueLabel())));

        return lines;
    };

    private static void functionCall(IRNode node, List<AssemblyLine> lines) {

        assert node instanceof IRCall;
        IRCall castedNode = (IRCall) node;

        /* Function Call Prologue */
        lines.add(new AssemblyComment("beginning of function call prologue"));
        // Save all caller-saved registers
        lines.add(new AssemblyComment("save all caller-saved registers"));
        AssemblyPhysicalRegister.saveToStack(lines, AssemblyFunction.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

        // pass pointer to return space as first argument (RDI)
        lines.add(new AssemblyComment("pass pointer to return space as first argument"));
        lines.add(new AssemblyInstruction(
                OpCode.MOVQ,
                AssemblyMemoryLocation.stackOffset(AssemblyFunction.getReturnValuesOffset()),
                AssemblyPhysicalRegister.RDI
        ));

        // pass pointer to additional argument space as second argument (RSI)
        lines.add(new AssemblyInstruction(
                OpCode.MOVQ,
                AssemblyMemoryLocation.stackOffset(AssemblyFunction.getArgumentsOffset()),
                AssemblyPhysicalRegister.RSI
        ));

        // put arguments in order rdx rcx r8 r9
        List<IRExpr> arguments = castedNode.args();

        for(int i = 0; i < arguments.size(); i++) {
            if (i < AssemblyPhysicalRegister.returnRegisters.length) {
                lines.add(
                        new AssemblyInstruction(
                                OpCode.MOVQ,
                                TileContainer.matchExpression(arguments.get(i), lines),
                                AssemblyPhysicalRegister.returnRegisters[i]
                        )
                );
            } else { // put into stack location
                 lines.add(
                    new AssemblyInstruction(
                            OpCode.MOVQ,
                            TileContainer.matchExpression(arguments.get(i), lines),
                            AssemblyMemoryLocation.stackOffset(AssemblyFunction.getArgumentsOffset()
                                    + Configuration.WORD_SIZE * i))
                );
           }
        }

         // get function name
        AssemblyExpression name = TileContainer.matchExpression(castedNode.target(), lines);
        assert name instanceof AssemblyName;

        // add the call instruction to lines
        lines.add(
                new AssemblyInstruction(
                        OpCode.CALLQ,
                        name
                )
        );

        functionCallEpilogue(lines);

    }

    private static void functionCallEpilogue(List<AssemblyLine> lines) {
        // now we restore all of the caller saved registers
        AssemblyPhysicalRegister.restoreFromStack(lines, AssemblyFunction.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

    }
}
