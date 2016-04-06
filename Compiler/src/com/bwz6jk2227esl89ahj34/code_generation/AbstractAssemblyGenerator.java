package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;
import java.util.LinkedList;
import java.util.List;

public class AbstractAssemblyGenerator {
    private TileContainer tileContainer = new TileContainer();

    /**
     * Add all instruction tiles to tileContainer
     */
    private void initialize() {
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
    }

    /**
     * Generate abstract assembly code for a program
     * @param root IRCompUnit of program
     * @return
     */
    public List<AssemblyInstruction> generate(IRCompUnit root) {

        return null;
    }

    /**
     * Generate abstract assembly code for a single function
     * @param func the IRFuncDecl to be translated to abstract assembly
     * @return
     */
    private List<AssemblyInstruction> generateFunction(IRFuncDecl func) {

        return null;
    }
}
