package com.bwz6jk2227esl89ahj34.ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.InsnMapsBuilder;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode {

    public IRNode visitChildren(IRVisitor v) {
        return this;
    }

    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        return v;
    }

    public IRNode buildInsnMaps(InsnMapsBuilder v) {
        v.addInsn(this);
        return this;
    }

    public abstract String label();

    public abstract void printSExp(SExpPrinter p);

    public abstract boolean containsCalls();

    /** Max number of results of any call in the subtree. */
    public abstract int computeMaximumCallResults();
}
