package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.LinkedList;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

public class StatementCodeGenerators {
    private TileContainer tileContainer = AbstractAssemblyGenerator.tileContainer;

    public StatementCodeGenerators(TileContainer tileContainer) {
        this.tileContainer = tileContainer;
    }

    StatementTile.CodeGenerator moveGenerator = (root) -> {
        /*
            MOVE(dst, src)
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRMove castedRoot = (IRMove) root;
        AssemblyExpression src = tileContainer.matchExpression(castedRoot.expr(), instructions);
        AssemblyExpression dst = tileContainer.matchExpression(castedRoot.target(), instructions);

        assert !(dst instanceof AssemblyImmediate);
        instructions.add(new AssemblyInstruction(OpCode.MOVEQ, src, dst));

        return instructions;
    };

    StatementTile.CodeGenerator jumpGenerator = (root) -> {
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

    StatementTile.CodeGenerator labelGenerator = (root) -> {
        /*
            LABEL(name)
         */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRLabel castedRoot = (IRLabel) root;
        AssemblyLabel label = new AssemblyLabel(new AssemblyName(castedRoot.name()));

        instructions.add(label);
        return instructions;
    };
    
}
