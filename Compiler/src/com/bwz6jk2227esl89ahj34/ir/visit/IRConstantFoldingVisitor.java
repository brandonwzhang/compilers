package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.IRConst;
import com.bwz6jk2227esl89ahj34.ir.IRNode;
import com.bwz6jk2227esl89ahj34.util.InternalCompilerError;

import java.math.BigInteger;


/**
 * Created by jihunkim on 3/17/16.
 */
public class IRConstantFoldingVisitor extends IRVisitor{
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
     * computes the IRBinOp into a IRConst
     * precondition: left and right are IRConst that we can fold over
     *
     * @param bop
     * @return
     */
    public static IRConst computeBinOp(IRBinOp bop) {
        BigInteger left = new BigInteger(""+((IRConst)(bop.left())).value());
        BigInteger right = new BigInteger(""+((IRConst)(bop.right())).value());
        switch(bop.opType()) {
            case ADD:
                return new IRConst(left.add(right).longValue());
            case SUB:
                return new IRConst(left.subtract(right).longValue());
            case MUL:
                return new IRConst(left.multiply(right).longValue());
            case HMUL:
                if (left.longValue() < 0) {
                    left = left.multiply(new BigInteger("-1"));
                }
                if (right.longValue() < 0) {
                    right = right.multiply(new BigInteger("-1"));
                }
                BigInteger temp = left.multiply(right).shiftRight(64);
                if (left.longValue() < 0) {
                    temp = temp.multiply(new BigInteger("-1"));
                }
                if (right.longValue() < 0) {
                    temp = temp.multiply(new BigInteger("-1"));
                }
                return new IRConst(temp.longValue());
            case DIV:
                return new IRConst(left.divide(right).longValue());
            case MOD:
                return new IRConst(left.mod(right).longValue());
            case AND:
                return new IRConst(left.and(right).longValue());
            case OR:
                return new IRConst(left.or(right).longValue());
            case XOR:
                return new IRConst(left.xor(right).longValue());
            case LSHIFT:
                return new IRConst(left.shiftLeft(right.intValue()).longValue());
            case RSHIFT:
                return new IRConst(left.longValue() >>> right.longValue());
            case ARSHIFT:
                return new IRConst(left.longValue() >> right.longValue());
            case EQ:
                return (left.equals(right)) ? new IRConst(1) : new IRConst(0);
            case NEQ:
                return (left.equals(right)) ? new IRConst(0) : new IRConst(1);
            case LT:
                return (left.compareTo(right) < 0) ? new IRConst(1)
                        : new IRConst(0);
            case GT:
                return (left.compareTo(right) > 0) ? new IRConst(1)
                        : new IRConst(0);
            case LEQ:
                return (left.compareTo(right) <= 0) ? new IRConst(1)
                        : new IRConst(0) ;
            case GEQ:
                return (left.compareTo(right) >= 0) ? new IRConst(1)
                        : new IRConst(0);
            default: // should not reach here
                throw new InternalCompilerError("Unknown op type");
        }
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
        if(n instanceof IRBinOp) {
            assert n_ instanceof IRBinOp;
            IRBinOp child = (IRBinOp)n_;
            if(child.left() instanceof IRConst
                    && child.right() instanceof IRConst) {
                // if divide by 0 do not constant fold
                // so it can fail at run time
                if (child.opType() == OpType.DIV
                        && ((IRConst) child.right()).value() == 0)  {
                    return n_;
                }
                IRConst result = computeBinOp(child);
                return result;
            } else {
                return n_;
            }
        } else {
            return n_;
        }
    }
}
