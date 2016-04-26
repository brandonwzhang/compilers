package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class AvailableExpressionsAnalysis extends DataflowAnalysis{
    public AvailableExpressionsAnalysis(IRSeq seq) {
        super(seq, Direction.FORWARD, allExprs(seq));
        // top = {all exprs}
        // bottom = {}
    }


    /**
     * Updates the node's equations as detailed below in comments.
     * @param node
     */
    public void transfer(CFGNode node) {
        assert node instanceof CFGNodeIR;

        // in[n] = intersection over all out[n'], where n' is pred of n
        Set<LatticeElement> pred_outs = new HashSet<>();
        for (CFGNode pred : node.getPredecessors()) {
            pred_outs.add(pred.getOut());
        }
        node.setIn(meet(pred_outs));

        // out[n] = in[n] U eval[n] - kill[n]
        Set<IRExpr> out = new HashSet<IRExpr>(((AvailableExpressionSet)node.getIn()).getExprs());
        out.addAll(eval(node).getExprs());
        kill(node, out); // removes kill[n]
        node.setOut(new AvailableExpressionSet(out));
    }


    /**
     * Applies the meet operator (intersection)
     * @param elements
     * @return
     */
    public LatticeElement meet(Set<LatticeElement> elements) {
        if (elements.isEmpty()) {
            return new AvailableExpressionSet(new HashSet<>());
        }

        AvailableExpressionSet meet = (AvailableExpressionSet)initial.copy();
        Set<IRExpr> exprs = meet.getExprs();
        for (LatticeElement e : elements) {
            AvailableExpressionSet set = (AvailableExpressionSet)e;
            exprs.retainAll(set.getExprs());
        }

        return meet;
    }

    /**
     * Returns the LatticeElement of eval[node]
     * @param node
     * @return
     */
     public AvailableExpressionSet eval(CFGNode node) {
         assert node instanceof CFGNodeIR;
         IRStmt stmt = ((CFGNodeIR) node).getStatement();
         assert stmt != null;

         Set<IRExpr> set = new HashSet<>();
         // Always lowered IR
         if (stmt instanceof IRMove) {
             IRMove move = (IRMove)stmt;

             // x = f(e....)
             if (move.expr() instanceof IRCall) {
                 for (IRExpr e : ((IRCall)move.expr()).args()) {
                     set.add(findReference(e));
                     set.addAll(findReferences(subexprs(e)));
                 }
             } else { // x = e
                 set.add(findReference(move.expr()));
                 set.addAll(findReferences(subexprs(move.expr())));
             }

             // [e1] = [e2]
             if (move.target() instanceof IRMem) {
                 set.add(findReference(move.target()));
                 set.addAll(findReferences(subexprs(move.target())));
             }
         }
         if (stmt instanceof IRCJump) {
             IRCJump cjump = (IRCJump) stmt;

             // if (e)
             set.add(findReference(cjump.expr()));
             set.addAll(findReferences(subexprs(cjump.expr())));
         }

         return new AvailableExpressionSet(set);

     }

    /**
     * Removes kill[node] from the set out
     * @param node
     * @param out
     */
    public void kill(CFGNode node, Set<IRExpr> out) {
        // destructively modify out to remove kill[node]
        IRStmt stmt = ((CFGNodeIR)node).getStatement();
        assert stmt != null;

        if (!(stmt instanceof IRMove)) {
            // We only kill upon encountering move
            return;
        }

        IRMove move = (IRMove) stmt;

        Predicate filter;
        if (move.target() instanceof IRTemp) {
            // Remove if expr contains any usage of x
            IRTemp x = (IRTemp)move.target();
            filter = e -> (e instanceof IRTemp) ? ((IRTemp)e).name().equals(x.name()) : false;
        } else if (move.target() instanceof IRMem || move.expr() instanceof IRCall) {
            // [e1] = e or x = f(e...)
            // Remove any expression with an [e'] where e' can alias e1
            // Remove any expression with an [e'] where f can affect [e']
            // We'll just remove any IRMem to be overly conservative
            filter = e -> e instanceof IRMem;
        } else {
            return;
        }

        // Remove any exprs that satisfy the filter
        for (Iterator<IRExpr> i = out.iterator(); i.hasNext();) {
            IRExpr expr = i.next();
            // remove if expr contains any usage of x
            if (containsCondition(expr, filter)) {
                i.remove();
            }
        }
    }

    // given a seq, return the "top"
    public static AvailableExpressionSet allExprs(IRSeq seq) {
        Set<IRExpr> set = new HashSet<>();
        for (IRStmt s : seq.stmts()) {
            if (s instanceof IRCJump) {
                IRCJump cjump = (IRCJump)s;
                addSubexprs(cjump.expr(), set);
            }
            if (s instanceof IRExp) {
                IRExp exp = (IRExp)s;
                addSubexprs(exp.expr(), set);
            }
            if (s instanceof IRJump) {
                IRJump jump = (IRJump)s;
                addSubexprs(jump.target(), set);
            }
            if (s instanceof IRMove) {
                IRMove move = (IRMove)s;
                addSubexprs(move.expr(), set);

                if (move.target() instanceof IRMem) {
                    addSubexprs(move.target(), set);
                }
            }
        }

        return new AvailableExpressionSet(set);
    }


    // Returns a set of expressions that are subexpressions of expr
    // Analogous to powerset
    private static Set<IRExpr> subexprs(IRExpr expr) {
        HashSet<IRExpr> set = new HashSet<>();
        addSubexprs(expr, set);
        return set;
    }

    // recursive helper function
    // precondition: IR is lowered
    private static void addSubexprs(IRExpr expr, Set<IRExpr> set) {
        set.add(expr);

        if (expr instanceof IRBinOp) {
            IRBinOp binop = (IRBinOp)expr;
            addSubexprs(binop.left(), set);
            addSubexprs(binop.right(), set);
        }
        if (expr instanceof IRCall) {
            IRCall call = (IRCall)expr;
            for (IRExpr e : call.args()) {
                addSubexprs(e, set);
            }
        }
        if (expr instanceof IRMem) {
            IRMem mem = (IRMem)expr;
            addSubexprs(mem.expr(), set);
        }
    }

    public static int exprSize(IRExpr expr) {
        if (expr instanceof IRBinOp) {
            IRBinOp binop = (IRBinOp)expr;
            return 1 + exprSize(binop.left()) + exprSize(binop.right());
        }
        if (expr instanceof IRCall) {
            int size = 1;
            IRCall call = (IRCall)expr;
            for (IRExpr arg : call.args()) {
                size += exprSize(arg);
            }
            return size;
        }
        if (expr instanceof IRMem) {
            IRMem mem = (IRMem)expr;
            return exprSize(mem.expr());
        }
        return 1;
    }


    /**
     * Takes in an IRExpr tree (lowered), and searches for a node that passes the predicate
     * specified by filter.
     * @param expr
     * @param filter
     * @return
     */
    private boolean containsCondition(IRExpr expr, Predicate filter) {
        if (expr instanceof IRCall) {
            for (IRExpr e : ((IRCall)expr).args()) {
                if (containsCondition(e, filter)) {
                    return true;
                }
            }
            return filter.test(expr);
        }
        if (expr instanceof IRMem) {
            return containsCondition(((IRMem)expr).expr(), filter) || filter.test(expr);
        }
        if (expr instanceof IRBinOp) {
            IRBinOp binop = (IRBinOp)expr;
            return containsCondition(binop.left(), filter) || containsCondition(binop.right(), filter) || filter.test(expr);
        }
        return filter.test(expr);
    }

    // helper function that returns if two IRExprs are structurally equal
    public boolean exprEquals(IRExpr e1, IRExpr e2) {
        if (!e1.getClass().equals(e2.getClass())) {
            return false;
        }

        if (e1 instanceof IRBinOp) {
            assert e2 instanceof IRBinOp;

            IRBinOp e1_ = (IRBinOp)e1;
            IRBinOp e2_ = (IRBinOp)e2;

            return e1_.opType().equals(e2_.opType()) &&
                    exprEquals(e1_.left(), e2_.left()) &&
                    exprEquals(e2_.right(), e2_.right());
        }
        if (e1 instanceof IRCall) {
            assert e2 instanceof IRCall;

            IRCall e1_ = (IRCall)e1;
            IRCall e2_ = (IRCall)e2;

            // compare function name
            if (!exprEquals(e1_.target(), e2_.target())) {
                return false;
            }
            // compare num of args
            if (e1_.args().size() != e2_.args().size()) {
                return false;
            }

            // compare all args
            boolean b = true;
            for (int i = 0; i < e1_.args().size(); i++) {
                b &= exprEquals(e1_.args().get(i), e2_.args().get(i));
            }

            return b;
        }
        if (e1 instanceof IRConst) {
            assert e2 instanceof IRConst;
            return ((IRConst)e1).value() == ((IRConst)e2).value();
        }
        if (e1 instanceof IRMem) {
            assert e2 instanceof IRMem;

            IRMem e1_ = (IRMem)e1;
            IRMem e2_ = (IRMem)e2;

            return exprEquals(e1_.expr(), e2_.expr());
        }
        if (e1 instanceof IRName) {
            assert e2 instanceof IRName;

            return ((IRName)e1).name().equals(((IRName)e2).name());
        }
        if (e1 instanceof IRTemp) {
            assert e2 instanceof IRTemp;

            return ((IRTemp)e1).name().equals(((IRTemp)e2).name());
        }


        return false;
    }

    /*
        Helper that given an expr e1, finds an e2 in top such that
        exprEquals(e1,e2) is true, and returns the reference to e2.
        Otherwise throw runtimeexception
     */
    public IRExpr findReference(IRExpr e1) {
        assert initial instanceof AvailableExpressionSet;

        for (IRExpr e2 : ((AvailableExpressionSet) initial).getExprs()) {
            if (exprEquals(e1, e2)) {
                return e2;
            }
        }
        throw new RuntimeException("Expression not found in top for " +
                "available expressions analysis. Please email esl89@cornell.edu");
    }

    /*
        Same helper as above, but does it for a set of IRExprs
     */
    public Set<IRExpr> findReferences(Set<IRExpr> exprs) {
        assert initial instanceof AvailableExpressionSet;

        Set<IRExpr> refs = new HashSet<>();
        for (IRExpr e : exprs) {
            refs.add(findReference(e));
        }

        return refs;
    }
}
