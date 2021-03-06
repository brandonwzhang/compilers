package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel, falseLabel)
 */
public class IRCJump extends IRStmt {
    private IRExpr expr;
    private String trueLabel, falseLabel;

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRExpr expr, String trueLabel) {
        this(expr, trueLabel, null);
    }

    /**
     *
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRExpr expr, String trueLabel, String falseLabel) {
        this.expr = expr;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    /**
     * Copy constructor (shallow).
     * @param cjump
     */
    public IRCJump(IRCJump cjump) {
        this.expr = cjump.expr;
        this.trueLabel = cjump.trueLabel;
        this.falseLabel = cjump.falseLabel;
    }

    public IRExpr expr() {
        return expr;
    }

    public String trueLabel() {
        return trueLabel;
    }

    public String falseLabel() {
        return falseLabel;
    }

    public boolean hasFalseLabel() {
        return falseLabel != null;
    }

    @Override
    public String label() {
        return "CJUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr) return new IRCJump(expr, trueLabel, falseLabel);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !hasFalseLabel();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CJUMP");
        expr.printSExp(p);
        p.printAtom(trueLabel);
        if (hasFalseLabel()) p.printAtom(falseLabel);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRCJump;
        assert ((IRCJump)(n_)).expr() instanceof IRESeq;
        IRESeq casted_eseq = (IRESeq)(((IRCJump)(n_)).expr());
        List<IRStmt> lst = new LinkedList<>();
        addStatements(lst, casted_eseq.stmt());
        addStatements(lst, new IRCJump(casted_eseq.expr(),
                ((IRCJump)(n_)).trueLabel(),
                ((IRCJump)(n_)).falseLabel()));
        return new IRSeq(lst);
    }
}
