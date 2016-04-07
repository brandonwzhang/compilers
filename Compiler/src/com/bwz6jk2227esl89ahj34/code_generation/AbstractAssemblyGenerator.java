package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbstractAssemblyGenerator {
    public static final TileContainer tileContainer = initialize();

    /**
     * Add all instruction tiles to tileContainer
     */
    private static TileContainer initialize() {
        return null;
    }

    /**
     * Generate abstract assembly code for a program
     * @param root IRCompUnit of program
     * @return
     */
    public static Map<String, List<AssemblyInstruction>> generate(IRCompUnit root) {
        Map<String, List<AssemblyInstruction>> functions = new LinkedHashMap<>();
        for (String functionName: root.functions().keySet()) {
            functions.put(functionName, generateFunction(root.functions().get(functionName)));
        }
        return functions;
    }

    /**
     * Generate abstract assembly code for a single function
     * @param func the IRFuncDecl to be translated to abstract assembly
     * @return
     */
    private static List<AssemblyInstruction> generateFunction(IRFuncDecl func) {
        IRSeq seq = (IRSeq) func.body();
        List<AssemblyInstruction> instructions = new LinkedList<>();
        for (IRStmt statement : seq.stmts()) {
            instructions.addAll(tileContainer.matchStatement(statement));
        }
        return instructions;
    }
}
