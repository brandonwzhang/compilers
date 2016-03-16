package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.MIRGenerateVisitor;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jihunkim on 3/14/16.
 */
public class MIRVisitor extends IRVisitor{
    // Counter to append to label strings.
    private long labelCounter = 0;

    private String getFreshVariable() {
        return "" + (labelCounter++);
    }
    /**
     * Recursively traverse the IR subtree rooted at {@code n}
     */
    public IRNode visit(IRNode node) {
        return visit(null, node);
    }

    /**
     * Allows to hijack the traversal of a subtree. This function is called by
     * {@link #visit(IRNode, IRNode)} upon entering node {@code n}.
     * If a non-null node {@code n0} is returned, the traversal is stopped
     * and the resulting AST has {@code n0} in place of {@code n}.
     *
     * By default, overriding is inactive.
     */
    protected IRNode override(IRNode parent, IRNode n) {
        return null;
    }

    /**
     * Called upon entering {@code n} during the AST traversal. This allows
     * to perform certain actions, including returning a new Node visitor to be
     * used in the subtree.
     */
    protected IRVisitor enter(IRNode parent, IRNode n) { return this; }

    /**
     * Called after finishing traversal of the subtree rooted at {@code n}.
     * When {@link #enter(IRNode, IRNode)} creates a new visitor to be used on
     * the subtree, the old visitor still receives the call to {@code leave()}
     * -- that is, {@code leave()} always executed the same number of times
     * as {@link #enter(IRNode, IRNode)}.
     * This node provides the final opportunity of placing an updated node
     * in the output AST.
     *
     * @param parent
     *            The parent AST node of {@code n} or {@code null}
     *            when it is the root.
     * @param n
     *            The original node in the input AST
     * @param n_
     *            The node returned by {@link IRNode#visitChildren(IRVisitor)}
     * @param v_
     *            The new node visitor created by
     *            {@link #enter(IRNode, IRNode)}, or {@code this}.
     */

    protected IRNode leave(IRNode parent, IRNode n, IRNode n_,
                           IRVisitor v_) {

        if (n instanceof IRSeq) {
            assert n_ instanceof IRSeq;
            List<IRStmt> flattenedResult = new LinkedList<>();
            for (IRStmt r : ((IRSeq)n_).stmts()) {
                if (r instanceof IRSeq) { //we need to flatten
                    for(IRStmt i: ((IRSeq)r).stmts()) {
                        flattenedResult.add(i);
                    }
                } else { //otherwise, unflatted
                    flattenedResult.add(r);
                }
            }
            return new IRSeq(flattenedResult);
        } else if (n instanceof IRExp) {
            assert n_ instanceof IRExp;
            assert ((IRExp)(n_)).expr() instanceof IRESeq;
            return new IRSeq(((IRESeq)((IRExp)(n_)).expr()).stmt());
//            assert n_ instanceof IRESeq; // L[[e]] = vector s; e'
//            return new IRSeq(((IRESeq)n_).stmt()); //L[[EXP(e)]] = vector s
        } else if (n instanceof IRMove) {
            assert n_ instanceof IRMove;
            assert ((IRMove)(n_)).target() instanceof IRESeq;
            assert ((IRMove)(n_)).expr() instanceof IRESeq;

            IRESeq casted_dest = (IRESeq)(((IRMove)(n_)).target());
            IRESeq casted_expr = (IRESeq)(((IRMove)(n_)).expr());

            List<IRStmt> lst = new LinkedList<>();
            lst.add(casted_expr.stmt());
            String t = getFreshVariable();
            lst.add(new IRMove(new IRTemp(t), casted_expr.expr()));
            lst.add(casted_dest.stmt());
            lst.add(new IRMove(casted_dest.expr(), new IRMem(new IRTemp(t))));
            return new IRSeq(lst); //TODO
        } else if (n instanceof IRConst) {
            return new IRESeq(new IRSeq(new LinkedList<IRStmt>()), (IRConst)n);
        } else if (n instanceof IRName) {
            return new IRESeq(new IRSeq(new LinkedList<IRStmt>()), (IRName)n);
        } else if (n instanceof IRTemp) {
            return new IRESeq(new IRSeq(new LinkedList<IRStmt>()),
                    new IRTemp(getFreshVariable()));
        } else if (n instanceof IRMem) {
            assert n_ instanceof IRMem;
            assert ((IRMem)n_).expr() instanceof IRESeq;
            IRESeq casted_eseq = (IRESeq)(((IRMem)n_).expr());
            return new IRESeq(casted_eseq.stmt(), new IRMem(casted_eseq.expr()));
        } else if (n instanceof IRJump) {
            assert n_ instanceof IRJump;
            assert ((IRJump)(n_)).target() instanceof IRESeq;
            IRESeq casted_eseq = (IRESeq)(((IRJump)(n_)).target());
            List<IRStmt> lst = new LinkedList<>();
            lst.add(casted_eseq.stmt());
            lst.add(new IRJump(casted_eseq.expr()));
            return new IRSeq(lst);
        } else if (n instanceof IRCJump) {
            assert n_ instanceof IRCJump;
            assert ((IRCJump)(n_)).expr() instanceof IRESeq;
            IRESeq casted_eseq = (IRESeq)(((IRCJump)(n_)).expr());
            List<IRStmt> lst = new LinkedList<>();
            lst.add(casted_eseq.stmt());
            lst.add(new IRLabel(((IRCJump)(n_)).trueLabel()));
            lst.add(new IRLabel(((IRCJump)(n_)).falseLabel()));
            return new IRSeq(lst);
        } else if (n instanceof IRESeq) {
            assert n_ instanceof IRESeq;
            IRESeq cast_n_ = (IRESeq)(n_);
            assert cast_n_.expr() instanceof IRESeq;
            IRESeq cast_eseq = (IRESeq)(cast_n_.expr());
            List<IRStmt> lst = new LinkedList<>();
            lst.add(cast_n_.stmt());
            lst.add(cast_eseq.stmt());
            return new IRESeq(new IRSeq(lst), cast_eseq.expr());
        } else if (n instanceof IRBinOp) {
            assert n_ instanceof IRBinOp;
            IRBinOp irb = (IRBinOp)(n_);
            assert irb.left() instanceof IRESeq;
            assert irb.right() instanceof IRESeq;
            List<IRStmt> lst = new LinkedList<>();
            lst.add(((IRESeq)(irb.left())).stmt());
            String t = getFreshVariable();
            lst.add(new IRMove(new IRTemp(t), ((IRESeq)(irb.left())).expr()));
            return new IRESeq(new IRSeq(lst),
                    new IRBinOp(irb.opType(), new IRTemp(t),
                            ((IRESeq)(irb.right())).expr()));
        } else if (n instanceof IRCall) {
            assert n_ instanceof IRCall;
            IRCall call_n_ = (IRCall)(n);
            List<IRStmt> stmtList = new LinkedList<>();
            List<IRExpr> tempList = new LinkedList<>();
            IRESeq eseq;
            IRTemp t;
            for (IRExpr e : call_n_.args()) {
                assert e instanceof IRESeq;
                eseq = (IRESeq)(e);
                stmtList.add(eseq.stmt());
                t = new IRTemp(getFreshVariable());
                stmtList.add(new IRMove(t, eseq.expr()));
                tempList.add(t);
            }
            t = new IRTemp(getFreshVariable());
            assert !tempList.isEmpty();
            IRExpr target = tempList.remove(0);
            stmtList.add(new IRMove(t , new IRCall(target, tempList)));
            return new IRESeq(new IRSeq(stmtList), t);
        } else {
            return n_;
        }
    }
}
