package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.IRFuncDecl;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;

import java.util.List;

public class CodeGenerator {
    private TileContainer tileContainer = new TileContainer();

    /**
     * Add all instruction tiles to tileContainer
     */
    private void initialize() {

    }

    /**
     * Generate abstract assembly code for a program
     * @param root IRCompUnit of program
     * @return
     */
    public List<AssemblyInstruction> generateAbstract(IRCompUnit root) {

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
