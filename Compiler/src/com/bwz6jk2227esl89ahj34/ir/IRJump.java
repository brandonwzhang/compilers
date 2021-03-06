package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for a transfer of control
 */
public class IRJump extends IRStmt {
    private IRExpr target;

    /**
     *
     * @param expr the destination of the jump
     */
    public IRJump(IRExpr expr) {
        target = expr;
    }

    /**
     * Copy constructor (shallow).
     * @param jump
     */
    public IRJump(IRJump jump) {
        this.target = jump.target;
    }

    public IRExpr target() {
        return target;
    }

    @Override
    public String label() {
        return "JUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, target);

        if (expr != target) return new IRJump(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("JUMP");
        target.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRJump;
        assert ((IRJump)(n_)).target() instanceof IRESeq;
        IRESeq casted_eseq = (IRESeq)(((IRJump)(n_)).target());
        List<IRStmt> lst = new LinkedList<>();
        addStatements(lst, casted_eseq.stmt());
        addStatements(lst, new IRJump(casted_eseq.expr()));
        return new IRSeq(lst);
    }
}
