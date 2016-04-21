package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRLowerVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    private IRExpr target;
    private IRExpr expr;

    /**
     *
     * @param target the destination of this move
     * @param expr the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr expr) {
        this.target = target;
        this.expr = expr;
    }

    /**
     * Copy constructor (shallow).
     * @param move
     */
    public IRMove(IRMove move) {
        this.target = move.target;
        this.expr = move.expr;
    }

    public IRExpr target() {
        return target;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "MOVE";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr target = (IRExpr) v.visit(this, this.target);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (target != this.target || expr != this.expr)
            return new IRMove(target, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("MOVE");
        target.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRMove;
        assert ((IRMove)(n_)).target() instanceof IRESeq;
        assert ((IRMove)(n_)).expr() instanceof IRESeq;

        IRESeq casted_dest = (IRESeq)(((IRMove)(n_)).target());
        IRESeq casted_expr = (IRESeq)(((IRMove)(n_)).expr());

        List<IRStmt> lst = new LinkedList<>();
        addStatements(lst, casted_expr.stmt());
        String t = MIRLowerVisitor.getFreshVariable();
        addStatements(lst, new IRMove(new IRTemp(t), casted_expr.expr()));
        addStatements(lst, casted_dest.stmt());
        addStatements(lst, new IRMove(casted_dest.expr(), new IRTemp(t)));
        return new IRSeq(lst);
    }
}
