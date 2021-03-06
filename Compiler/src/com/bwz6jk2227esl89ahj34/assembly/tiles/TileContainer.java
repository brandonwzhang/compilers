package com.bwz6jk2227esl89ahj34.assembly.tiles;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyExpression;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import com.bwz6jk2227esl89ahj34.ir.IRNode;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;

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

        add(new ExpressionTile(ExpressionPatterns.mem2, ExpressionCodeGenerators.mem234));
        add(new ExpressionTile(ExpressionPatterns.mem3, ExpressionCodeGenerators.mem234));
        add(new ExpressionTile(ExpressionPatterns.mem4, ExpressionCodeGenerators.mem234));
        add(new ExpressionTile(ExpressionPatterns.mem5, ExpressionCodeGenerators.mem5));

        add(new StatementTile(StatementPatterns.move1, StatementCodeGenerators.move1));
        add(new StatementTile(StatementPatterns.move2, StatementCodeGenerators.move2));
        add(new StatementTile(StatementPatterns.jump1, StatementCodeGenerators.jump1));
        add(new StatementTile(StatementPatterns.label1, StatementCodeGenerators.label1));
        add(new StatementTile(StatementPatterns.exp1, StatementCodeGenerators.exp1));
        add(new StatementTile(StatementPatterns.return1, StatementCodeGenerators.return1));
        add(new StatementTile(StatementPatterns.cjump1, StatementCodeGenerators.cjump1));

        add(new NontrivialStatementTile(StatementPatterns.move3, StatementCodeGenerators.move3456));
        add(new NontrivialStatementTile(StatementPatterns.move4, StatementCodeGenerators.move3456));
        add(new NontrivialStatementTile(StatementPatterns.move5, StatementCodeGenerators.move3456));
        add(new NontrivialStatementTile(StatementPatterns.move6, StatementCodeGenerators.move3456));

        add(new NontrivialStatementTile(StatementPatterns.move7, StatementCodeGenerators.move78910));
        add(new NontrivialStatementTile(StatementPatterns.move8, StatementCodeGenerators.move78910));
        add(new NontrivialStatementTile(StatementPatterns.move9, StatementCodeGenerators.move78910));
        add(new NontrivialStatementTile(StatementPatterns.move10, StatementCodeGenerators.move78910));
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

        throw new RuntimeException("No assembly matched! Please email jk2227@cornell.edu");
    }

    public static AssemblyExpression matchExpression(IRNode root, List<AssemblyLine> lines) {
        assert root instanceof IRExpr;

        for (ExpressionTile tile : exprTiles) {
            if (tile.match(root)) {
                return tile.codeGenerator.generate(root, lines); // imperatively updates assemblyCode
            }
        }

        throw new RuntimeException("No assembly matched! Please email bwz6@cornell.edu");
    }


    private static class TileComparator implements Comparator<Tile> {
        @Override
        public int compare(Tile t1, Tile t2) {
            if (t2.size - t1.size == 0 ) return -1;
            return t2.size - t1.size; // order by highest size first
        }
    }

}

