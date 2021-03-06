package com.bwz6jk2227esl89ahj34.assembly.tiles;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyExpression;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ir.IRNode;

import java.util.List;

public class ExpressionTile extends Tile {
    CodeGenerator codeGenerator;

    public ExpressionTile(IRNode pattern, CodeGenerator codeGenerator) {
        super(pattern);
        this.codeGenerator = codeGenerator;
    }
    public interface CodeGenerator {
        AssemblyExpression generate(IRNode root, List<AssemblyLine> lines);
    }
}
