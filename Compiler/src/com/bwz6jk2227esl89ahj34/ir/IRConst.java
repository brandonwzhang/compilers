package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
    public boolean isConstant() {
        return true;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        return new IRESeq(new IRSeq(new LinkedList<>()), (IRConst)n);
    }
}
