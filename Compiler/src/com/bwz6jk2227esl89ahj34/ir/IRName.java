package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

import java.util.LinkedList;

/**
 * An intermediate representation for named memory address
 * NAME(n)
 */
public class IRName extends IRExpr {
    private String name;
    private boolean isData = false;

    /**
     *
     * @param name name of this memory address
     */
    public IRName(String name) {
        this.name = name;
    }

    public IRName(String name, boolean isData) {
        this.name = name;
        this.isData = isData;
    }

    /**
     * Copy constructor (shallow).
     * @param name
     */
    public IRName(IRName name) {
        this.name = name.name;
    }

    public String name() {
        return name;
    }

    public boolean isData() {
        return isData;
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
        p.printAtom(name);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        return new IRESeq(new IRSeq(new LinkedList<>()), (IRName)n);
    }
}
