package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

public class ExpressionCodeGenerators {
    TileContainer tileContainer = AbstractAssemblyGenerator.tileContainer;

    ExpressionTile.CodeGenerator constGenerator = (root, instructions) -> {
            /*
                CONST(i)
             */
        IRConst castedRoot = (IRConst) root;
        return new AssemblyImmediate(castedRoot.value());
    };

    ExpressionTile.CodeGenerator tempGenerator = (root, instructions) -> {
            /*
                TEMP(t)
             */
        IRTemp castedRoot = (IRTemp) root;
        return new AssemblyAbstractRegister(castedRoot);
    };

    ExpressionTile.CodeGenerator memGenerator = (root, instructions) -> {
            /*
                MEM(e)
             */
        IRMem castedRoot = (IRMem) root;
        AssemblyExpression e = tileContainer.matchExpression(castedRoot.expr(), instructions);
        assert e instanceof AssemblyAbstractRegister;

        return new AssemblyMemoryLocation((AssemblyAbstractRegister)e);
    };
}
