package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;

import java.util.LinkedList;
import java.util.List;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

public class StatementCodeGenerators {
    private static TileContainer tileContainer = AbstractAssemblyGenerator.tileContainer;

    public StatementCodeGenerators(TileContainer tileContainer) {
        this.tileContainer = tileContainer;
    }

    public static StatementTile.CodeGenerator move1 = (root) -> {
        /*
            MOVE(dst, src)
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRMove castedRoot = (IRMove) root;
        AssemblyExpression src = tileContainer.matchExpression(castedRoot.expr(), instructions);
        AssemblyExpression dst = tileContainer.matchExpression(castedRoot.target(), instructions);

        assert !(dst instanceof AssemblyImmediate);
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, src, dst));

        return instructions;
    };

    public static StatementTile.CodeGenerator jump1 = (root) -> {
        /*
            JUMP(label)
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRJump castedRoot = (IRJump) root;
        AssemblyExpression label = tileContainer.matchExpression(castedRoot.target(), instructions);

        assert label instanceof AssemblyName;
        instructions.add(new AssemblyInstruction(OpCode.JMP, label));

        return instructions;
    };

    public static StatementTile.CodeGenerator label1 = (root) -> {
        /*
            LABEL(name)
         */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRLabel castedRoot = (IRLabel) root;
        AssemblyLabel label = new AssemblyLabel(new AssemblyName(castedRoot.name()));

        instructions.add(label);
        return instructions;
    };

    public static StatementTile.CodeGenerator exp1 = (root) -> {
      	/*
        		EXP(Call(Label))
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRExp castedRoot = (IRExp) root;
        // TODO
        return instructions;
    };

    public static StatementTile.CodeGenerator move2 = (root) -> {
    		/*
        		MOVE(TEMP, CALL(LABEL)
        */
        return null;
    };

    /**
     * for convenience, we take care of the "function epilogue"
     * along with the return statement 
     */
    public static StatementTile.CodeGenerator return1 = (root) -> {
        /*
        		RETURN()
        */
        List<AssemblyInstruction> instructions = new LinkedList<>();
        // Restore callee-save registers
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.R15)));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.R14)));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.R13)));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.R12)));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.RBP)));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, new AssemblyPhysicalRegister(Register.RBX)));
        // Restore old RBP and RSP
        AssemblyPhysicalRegister rbp = new AssemblyPhysicalRegister(Register.RBP);
        AssemblyPhysicalRegister rsp = new AssemblyPhysicalRegister(Register.RSP);
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, rbp, rsp));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, rbp));
        instructions.add(new AssemblyInstruction(OpCode.RETQ));
        return instructions;
    };

    public static StatementTile.CodeGenerator cjump1 = (root) -> {
        /*
            CJUMP(e, trueLabel)
         */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRCJump castedRoot = (IRCJump) root;
        assert castedRoot.falseLabel() == null; // assert lowered CJump
        AssemblyExpression guard = tileContainer.matchExpression(castedRoot.expr(), instructions);

        // compare guard to 0, jump to trueLabel if not equal
        instructions.add(new AssemblyInstruction(OpCode.CMP, guard, new AssemblyImmediate(0)));
        instructions.add(new AssemblyInstruction(OpCode.JNE, new AssemblyName(castedRoot.trueLabel())));

        return instructions;
    };

    private static AssemblyExpression functionCall(IRNode node, List<AssemblyInstruction> instructions) {

        return null;
    }
}
