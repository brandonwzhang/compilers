package com.bwz6jk2227esl89ahj34.ir.visit;


import com.bwz6jk2227esl89ahj34.ir.*;

public class IRCopyVisitor extends IRVisitor{

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
        if (n instanceof IRBinOp) {

        }
        return n;
    }

}
