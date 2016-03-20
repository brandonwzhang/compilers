package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;

/**
 * An intermediate representation for evaluating an expression for side effects,
 * discarding the result
 * EXP(e)
 */
public class IRExp extends IRStmt {
    private IRExpr expr;

    /**
     *
     * @param expr the expression to be evaluated and result discarded
     */
    public IRExp(IRExpr expr) {
        this.expr = expr;
    }

    /**
     * Copy constructor (shallow).
     * @param exp
     */
    public IRExp(IRExp exp) {
        this.expr = exp.expr;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "EXP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr) return new IRExp(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("EXP");
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRExp;
        assert ((IRExp)(n_)).expr() instanceof IRESeq;
        return ((IRESeq)((IRExp)(n_)).expr()).stmt();
    }
}
