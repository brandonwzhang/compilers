package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr extends IRNode {

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterExpr();
    }

    public boolean isConstant() {
        return false;
    }
}