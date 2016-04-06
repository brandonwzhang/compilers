package com.bwz6jk2227esl89ahj34.AST.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.List;

public class ExpressionTile extends Tile {
    CodeGenerator codeGenerator;

    public ExpressionTile(IRNode pattern, TileContainer tileContainer, CodeGenerator codeGenerator) {
        super(pattern, tileContainer);
        this.codeGenerator = codeGenerator;
    }
    public interface CodeGenerator {
        AssemblyExpression generate(IRNode root, List<AssemblyInstruction> instructions);
    }
}
