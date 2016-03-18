package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.util.SExpPrinter;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
public class IRConst extends IRExpr {
    private long value;

    /**
     *
     * @param value value of this constant
     */
    public IRConst(long value) {
        this.value = value;
    }

    /**
     * Copy constructor (shallow).
     * @param c
     */
    public IRConst(IRConst c) {
        this.value = c.value;
    }

    public long value() {
        return value;
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
        p.endList();
    }
}
