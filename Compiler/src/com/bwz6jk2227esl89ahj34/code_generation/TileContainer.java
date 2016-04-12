package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class TileContainer {
    private static TreeSet<StatementTile> stmtTiles = new TreeSet<>(new TileComparator());
    private static TreeSet<ExpressionTile> exprTiles = new TreeSet<>(new TileComparator());

    // Add all tiles to the container
    static {
        add(new ExpressionTile(ExpressionPatterns.const1, ExpressionCodeGenerators.const1));
        add(new ExpressionTile(ExpressionPatterns.temp1, ExpressionCodeGenerators.temp1));
        add(new ExpressionTile(ExpressionPatterns.mem1, ExpressionCodeGenerators.mem1));
        add(new ExpressionTile(ExpressionPatterns.name1, ExpressionCodeGenerators.name1));
        add(new ExpressionTile(ExpressionPatterns.binop1, ExpressionCodeGenerators.binop1));

        add(new StatementTile(StatementPatterns.move1, StatementCodeGenerators.move1));
        add(new StatementTile(StatementPatterns.move2, StatementCodeGenerators.move2));
        add(new StatementTile(StatementPatterns.jump1, StatementCodeGenerators.jump1));
        add(new StatementTile(StatementPatterns.label1, StatementCodeGenerators.label1));
        add(new StatementTile(StatementPatterns.exp1, StatementCodeGenerators.exp1));
        add(new StatementTile(StatementPatterns.return1, StatementCodeGenerators.return1));
        add(new StatementTile(StatementPatterns.cjump1, StatementCodeGenerators.cjump1));
    }

    public static boolean add(Tile tile) {
        if (tile instanceof StatementTile) {
            return stmtTiles.add((StatementTile)tile);
        }
        else {
            return exprTiles.add((ExpressionTile)tile);
        }
    }

    public static List<AssemblyLine> matchStatement(IRNode root) {
        assert root instanceof IRStmt;
        for (StatementTile tile : stmtTiles) {
            if (tile.match(root)) {
                return tile.codeGenerator.generate(root);
            }
        }

        throw new RuntimeException("No code_generation matched! Please email jk2227@cornell.edu");
    }

    public static AssemblyExpression matchExpression(IRNode root, List<AssemblyLine> lines) {
        assert root instanceof IRExpr;

        for (ExpressionTile tile : exprTiles) {
            if (tile.match(root)) {
                return tile.codeGenerator.generate(root, lines); // imperatively updates assemblyCode
            }
        }

        throw new RuntimeException("No code_generation matched! Please email bwz6@cornell.edu");
    }


    private static class TileComparator implements Comparator<Tile> {
        @Override
        public int compare(Tile t1, Tile t2) {
            if (t2.size - t1.size == 0 ) return -1;
            return t2.size - t1.size; // order by highest size first
        }
    }

}

