package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.List;

public class StatementTile extends Tile {
    CodeGenerator codeGenerator;

    public StatementTile(IRNode pattern, TileContainer tileContainer, CodeGenerator codeGenerator) {
        super(pattern, tileContainer);
        this.codeGenerator = codeGenerator;
    }
    public interface CodeGenerator {
        List<AssemblyInstruction> generate(IRNode root);
    }
}
