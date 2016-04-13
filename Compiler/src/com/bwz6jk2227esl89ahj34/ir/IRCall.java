package com.bwz6jk2227esl89ahj34.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.bwz6jk2227esl89ahj34.ir.visit.MIRLowerVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr {
    private IRExpr target;
    private List<IRExpr> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, IRExpr... args) {
        this(target, Arrays.asList(args));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, List<IRExpr> args) {
        this.target = target;
        this.args = args;
    }

    /**
     * Copy constructor (shallow).
     * @param call
     */
    public IRCall(IRCall call) {
        this.target = call.target;
        this.args = call.args;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    @Override
    public String label() {
        return "CALL";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        IRExpr target = (IRExpr) v.visit(this, this.target);
        if (target != this.target) modified = true;

        List<IRExpr> results = new ArrayList<>(args.size());
        for (IRExpr arg : args) {
            IRExpr newExpr = (IRExpr) v.visit(this, arg);
            if (newExpr != arg) modified = true;
            results.add(newExpr);
        }

        if (modified) return new IRCall(target, results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        for (IRExpr arg : args)
            result = v.bind(result, v.visit(arg));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inExpr();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CALL");
        target.printSExp(p);
        for (IRExpr arg : args)
            arg.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRCall;
        IRCall call_n_ = (IRCall)(n_);
        List<IRStmt> stmtList = new LinkedList<>();
        List<IRExpr> tempList = new LinkedList<>();

        IRESeq eseq;
        IRTemp t;

        assert call_n_.target() instanceof IRESeq;
        eseq = (IRESeq)(call_n_.target());
        IRESeq og =  (IRESeq)(call_n_.target());
        addStatements(stmtList, eseq.stmt());

        for (IRExpr e : call_n_.args()) {
            assert e instanceof IRESeq;
            eseq = (IRESeq) (e);
            addStatements(stmtList, eseq.stmt());
            t = new IRTemp(MIRLowerVisitor.getFreshVariable());
            addStatements(stmtList, new IRMove(t, eseq.expr()));
            tempList.add(t);
        }

        t = new IRTemp(MIRLowerVisitor.getFreshVariable());
        assert og.expr() instanceof IRName;
            addStatements(stmtList,
                    new IRMove(t, new IRCall((IRName)(og.expr()), tempList)));
        return new IRESeq(new IRSeq(stmtList), t);
    }
}
