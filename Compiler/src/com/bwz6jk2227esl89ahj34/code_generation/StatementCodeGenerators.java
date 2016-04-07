package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.LinkedList;
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

    public static StatementTile.CodeGenerator exp = (root) -> {
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

    public static StatementTile.CodeGenerator return1 = (root) -> {
        /*
        		RETURN()
        */
        return null;
    };

    public static StatementTile.CodeGenerator cjump1 = (root) -> {
        return null;
    };
}
