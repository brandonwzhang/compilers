package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.dataflow_analysis
        .conditional_constant_propagation.Value;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.Map;

/**
 * Created by jihunkim on 4/26/16.
 */
public class ConditionalConstantPropagationVisitor extends IRVisitor {
    private Map<String, LatticeElement> valueTuples;

    public ConditionalConstantPropagationVisitor(Map<String, LatticeElement> map) {
        this.valueTuples = map;
    }

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
        if (n_ instanceof IRMove) {
            IRMove castedMove = (IRMove) n_;
            if (castedMove.target() instanceof IRConst) {
                IRExpr newSrc = castedMove.expr();
                return new IRMove(((IRMove)n).target(), newSrc);
            } else {
                return n_;
            }
        } else if (n_ instanceof IRTemp) {
            return temp((IRTemp)n_);
        } else if (n_ instanceof IRBinOp) {
            IRBinOp casted = (IRBinOp) n_;
            IRExpr left = casted.left();
            IRExpr right = casted.right();
            if (left instanceof IRTemp) {
                left = temp((IRTemp) left);
            }
            if (right instanceof IRTemp) {
                right = temp((IRTemp) right);
            }
            IRConstantFoldingVisitor v = new IRConstantFoldingVisitor();
            return v.visit(new IRBinOp(casted.opType(), left, right));
        } else {
            return n_;
        }
    }

    private IRExpr temp(IRTemp n_) {
        String tempName = n_.name();
        if (valueTuples.containsKey(tempName)) {
            LatticeElement tuples = valueTuples.get(tempName);
            if (tuples instanceof LatticeBottom || tuples instanceof LatticeTop) {
                return n_;
            } else {
                return new IRConst(((Value)tuples).getValue().value());
            }
        } else {
            return n_;
        }
    }
}
