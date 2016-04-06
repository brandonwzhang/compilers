package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

public abstract class Tile {
    public IRNode pattern;
    public int size;
    public TileContainer tileContainer;

    public Tile(IRNode pattern, TileContainer tileContainer) {
        this.pattern = pattern;
        this.size = getPatternSize(pattern);
        this.tileContainer = tileContainer;
        tileContainer.addTile(this);
    }


    /**
     * If root matches the pattern, return true
     * otherwise return false
     */
    public boolean match(IRNode root) {
        return matchHelper(root, pattern);
    }

    /**
     * Recursive helper method for match
     */
    public boolean matchHelper(IRNode curRoot, IRNode curPattern) {
        assert curRoot != null;
        // if curPattern is null, we have reached the end of the pattern
        // without returning false, so we can safely return true
        if (curPattern == null) { return true; }

        // if pattern does not match, we return false
        if (!curRoot.getClass().equals(curPattern.getClass())) {
            return false;
        }

        if (curRoot instanceof IRBinOp) {
            IRBinOp castedRoot = (IRBinOp) curRoot;
            IRBinOp castedPattern = (IRBinOp) curPattern;
            return matchHelper(castedRoot.left(), castedPattern.left())
                    && matchHelper(castedRoot.right(), castedPattern.right());
        } else if (curRoot instanceof IRCall) { // need to check if content wrapped as target are same type
            IRCall castedRoot = (IRCall) curRoot;
            IRCall castedPattern = (IRCall) curPattern;
            return matchHelper(castedRoot.target(), castedPattern.target());
        } else if (curRoot instanceof IRCJump) {
            return true;
        } else if (curRoot instanceof IRCompUnit) {
            assert false : "shouldn't reach IRCompUnit";
            return false;
        } else if (curRoot instanceof IRConst) {
            return true;
        } else if (curRoot instanceof IRExp) { // need to check if content wrapped by IRExp is same
            IRExp castedRoot = (IRExp) curRoot;
            IRExp castedPattern = (IRExp) curPattern;
            return matchHelper(castedRoot.expr(), castedPattern.expr());
        } else if (curRoot instanceof IRFuncDecl) {
            assert false : "shouldn't reach IRFuncDecl";
            return false;
        } else if (curRoot instanceof IRJump) {
            IRJump castedRoot = (IRJump) curRoot;
            IRJump castedPattern = (IRJump) curPattern;
            return matchHelper(castedRoot.target(), castedPattern.target());
        } else if (curRoot instanceof IRLabel) {
            return true;
        } else if (curRoot instanceof IRMem) {
            IRMem castedRoot = (IRMem) curRoot;
            IRMem castedPattern = (IRMem) curPattern;
            if (castedRoot.memType() != castedPattern.memType()) {
                return false;
            }
            return matchHelper(castedRoot.expr(), castedPattern.expr());
        } else if (curRoot instanceof IRMove) {
            IRMove castedRoot = (IRMove) curRoot;
            IRMove castedPattern = (IRMove) curPattern;
            return matchHelper(castedRoot.target(), castedPattern.target())
                    && matchHelper(castedRoot.expr(), castedPattern.expr());
        } else if (curRoot instanceof IRName) {
            return true;
        } else if (curRoot instanceof IRReturn) {
            return true;
        } else if (curRoot instanceof IRTemp) {
            return true;
        } else {
            assert false : "Invalid IRNode encountered";
            return false;
        }
    }

    /**
     * Returns the number of nodes in a tile pattern
     */
    public int getPatternSize(IRNode curRoot) {
        if (curRoot instanceof IRBinOp) {
            IRBinOp castedRoot = (IRBinOp) curRoot;
            return 1 + getPatternSize(castedRoot.left()) + getPatternSize(castedRoot.right());
        } else if (curRoot instanceof IRCall) { // need to check if content wrapped as target are same type
            return 1;
        } else if (curRoot instanceof IRCJump) {
            return 1;
        } else if (curRoot instanceof IRConst) {
            return 1;
        } else if (curRoot instanceof IRExp) { // need to check if content wrapped by IRExp is same
            IRExp castedRoot = (IRExp) curRoot;
            return 1 + getPatternSize(castedRoot.expr());
        } else if (curRoot instanceof IRJump) {
            IRJump castedRoot = (IRJump) curRoot;
            return 1 + getPatternSize(castedRoot.target());
        } else if (curRoot instanceof IRLabel) {
            return 1;
        } else if (curRoot instanceof IRMem) {
            IRMem castedRoot = (IRMem) curRoot;
            return 1 + getPatternSize(castedRoot.expr());
        } else if (curRoot instanceof IRMove) {
            IRMove castedRoot = (IRMove) curRoot;
            return 1 + getPatternSize(castedRoot.target());
        } else if (curRoot instanceof IRName) {
            return 1;
        } else if (curRoot instanceof IRReturn) {
            return 1;
        } else if (curRoot instanceof IRTemp) {
            return 1;
        } else {
            assert false : "Invalid IRNode encountered";
            return 0;
        }
    }
}

