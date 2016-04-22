package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr {
    private IRStmt stmt;
    private IRExpr expr;

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRStmt stmt, IRExpr expr) {
        this.stmt = stmt;
        this.expr = expr;
    }

    /**
     * Copy constructor (shallow).
     * @param eseq
     */
    public IRESeq(IRESeq eseq) {
        this.stmt = eseq.stmt;
        this.expr = eseq.expr;
    }

    public IRStmt stmt() {
        return stmt;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "ESEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, this.stmt);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr || stmt != this.stmt)
            return new IRESeq(stmt, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(stmt));
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return false;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("ESEQ");
        stmt.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRESeq;
        IRESeq cast_n_ = (IRESeq)(n_);
        assert cast_n_.expr() instanceof IRESeq;
        IRESeq cast_eseq = (IRESeq)(cast_n_.expr());
        List<IRStmt> lst = new LinkedList<>();
        addStatements(lst, cast_n_.stmt());
        addStatements(lst, cast_eseq.stmt());
        return new IRESeq(new IRSeq(lst), cast_eseq.expr());
    }
}
