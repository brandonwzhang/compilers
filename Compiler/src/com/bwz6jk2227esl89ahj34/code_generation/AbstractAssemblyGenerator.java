package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.List;

public class AbstractAssemblyGenerator {
    private TileContainer tileContainer = new TileContainer();

    /**
     * Add all instruction tiles to tileContainer
     */
    private void initialize() {
        ExpressionTile.CodeGenerator constGenerator = (root, instructions) -> {
            IRConst castedRoot = (IRConst) root;
            return new AssemblyImmediate(castedRoot.value());
        };

        ExpressionTile.CodeGenerator tempGenerator = (root, instructions) -> {
            IRTemp castedRoot = (IRTemp) root;
            return new AssemblyAbstractRegister(castedRoot);
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
