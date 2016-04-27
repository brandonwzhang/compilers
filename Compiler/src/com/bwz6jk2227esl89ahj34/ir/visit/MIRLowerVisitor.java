package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.ir.IRNode;

public class MIRLowerVisitor extends IRVisitor{
    // Counter to append to label strings.
    private static long labelCounter = 0;

    public static String getFreshVariable() {
        return "low" + (labelCounter++);
    }

    /**
     * Lowers a node after its children have been lowered.
     */
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        return n.leave(v_, n, n_);
    }

}
