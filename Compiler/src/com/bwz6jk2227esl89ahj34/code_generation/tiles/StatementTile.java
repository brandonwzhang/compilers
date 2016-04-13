package com.bwz6jk2227esl89ahj34.code_generation.tiles;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.List;

public class StatementTile extends Tile {
    CodeGenerator codeGenerator;

    public StatementTile(IRNode pattern, CodeGenerator codeGenerator) {
        super(pattern);
        this.codeGenerator = codeGenerator;
    }
    public interface CodeGenerator {
        List<AssemblyLine> generate(IRNode root);
    }
}
