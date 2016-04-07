package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

class TileContainer {
    TreeSet<StatementTile> stmtTiles;
    TreeSet<ExpressionTile> exprTiles;

    public TileContainer() {
        // these will be populated in the Tile constructor by calling addTile
        stmtTiles = new TreeSet<>(new TileComparator());
        exprTiles = new TreeSet<>(new TileComparator());
    }

    public boolean add(Tile tile) {
        if (tile instanceof StatementTile) {
            return stmtTiles.add((StatementTile)tile);
        }
        else {
            return exprTiles.add((ExpressionTile)tile);
        }
    }

    public List<AssemblyInstruction> matchStatement(IRNode root) {
        assert root instanceof IRStmt;
        for (StatementTile tile : stmtTiles) {
            if (tile.match(root)) {
                return tile.codeGenerator.generate(root);
            }
        }

        throw new RuntimeException("No code_generation matched! Please email jk2227@cornell.edu");
    }

    public AssemblyExpression matchExpression(IRNode root, List<AssemblyInstruction> assemblyCode) {
        assert root instanceof IRExpr;

        for (ExpressionTile tile : exprTiles) {
            if (tile.match(root)) {
                return tile.codeGenerator.generate(root, assemblyCode); // imperatively updates assemblyCode
            }
        }

        throw new RuntimeException("No code_generation matched! Please email bwz6@cornell.edu");
    }


    private class TileComparator implements Comparator<Tile> {
        @Override
        public int compare(Tile t1, Tile t2) {
            return t2.size - t1.size; // order by highest size first
        }
    }

}

