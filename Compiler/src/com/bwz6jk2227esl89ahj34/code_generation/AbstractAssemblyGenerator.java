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
        TileContainer tileContainer = new TileContainer();
        tileContainer.add(new ExpressionTile(ExpressionPatterns.const1, ExpressionCodeGenerators.const1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.temp1, ExpressionCodeGenerators.temp1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.mem1, ExpressionCodeGenerators.mem1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.call1, ExpressionCodeGenerators.call1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.name1, ExpressionCodeGenerators.name1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.binop1, ExpressionCodeGenerators.binop1));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.binop2, ExpressionCodeGenerators.binop2));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.binop3, ExpressionCodeGenerators.binop3));
        tileContainer.add(new ExpressionTile(ExpressionPatterns.binop4, ExpressionCodeGenerators.binop4));

        tileContainer.add(new StatementTile(StatementPatterns.move1, StatementCodeGenerators.move1));
        tileContainer.add(new StatementTile(StatementPatterns.move2, StatementCodeGenerators.move2));
        tileContainer.add(new StatementTile(StatementPatterns.jump1, StatementCodeGenerators.jump1));
        tileContainer.add(new StatementTile(StatementPatterns.label1, StatementCodeGenerators.label1));
        tileContainer.add(new StatementTile(StatementPatterns.exp1, StatementCodeGenerators.exp1));
        tileContainer.add(new StatementTile(StatementPatterns.return1, StatementCodeGenerators.return1));
        tileContainer.add(new StatementTile(StatementPatterns.cjump1, StatementCodeGenerators.cjump1));
        return tileContainer;
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