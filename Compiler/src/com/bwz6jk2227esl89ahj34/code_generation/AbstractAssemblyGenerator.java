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
        ExpressionCodeGenerators expressionCodeGenerators =
                new ExpressionCodeGenerators(tileContainer);
        StatementCodeGenerators statementCodeGenerators =
                new StatementCodeGenerators(tileContainer);
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
