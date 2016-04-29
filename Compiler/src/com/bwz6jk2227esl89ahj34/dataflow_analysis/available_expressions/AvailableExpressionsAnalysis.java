package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNodeIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet.TaggedExpression;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class AvailableExpressionsAnalysis extends DataflowAnalysis{
    public static class ExpressionNodePair {
        public IRExpr expr;
        public CFGNode node;

        public ExpressionNodePair(IRExpr expr, CFGNode node) {
            this.expr = expr;
            this.node = node;
        }

        public ExpressionNodePair(TaggedExpression taggedExpr) {
            this.expr = taggedExpr.expr;
            this.node = taggedExpr.node;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ExpressionNodePair)) {
                return false;
            }
            ExpressionNodePair pair = (ExpressionNodePair) o;
            return pair.expr.equals(expr) &&
                    pair.node.equals(node);
        }

        @Override
        public int hashCode() {
            return expr.hashCode() + node.hashCode();
        }
    }

    public Set<IRExpr> allExprs;

    public AvailableExpressionsAnalysis(IRSeq seq) {
        // We initialize all nodes to the bottom element, {}
        super(seq, Direction.FORWARD, new AvailableExpressionSet(new HashSet<>()), false);
        allExprs = allExprs(seq);
        fixpoint(Direction.FORWARD);
    }

    /**
     * Returns all expressions in seq
     */
    public static Set<IRExpr> allExprs(IRSeq seq) {
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
        return set;
    }

    /**
     * Updates the node's equations as detailed below in comments.
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
        Set<TaggedExpression> out = new HashSet<>(((AvailableExpressionSet)node.getIn()).getExprs());
        out.addAll(eval(node).getExprs());
        kill(node, out); // removes kill[n]
        node.setOut(new AvailableExpressionSet(out));
    }


    /**
     * Applies the meet operator (intersection)
     */
    public LatticeElement meet(Set<LatticeElement> elements) {
        if (elements.isEmpty()) {
            return new AvailableExpressionSet(new HashSet<>());
        }

        Set<TaggedExpression> exprs = null;
        for (LatticeElement e : elements) {
            Set<TaggedExpression> set = ((AvailableExpressionSet) e).getExprs();
            if (exprs == null) {
                exprs = set;
            } else {
                exprs.retainAll(set);
            }
        }

        return new AvailableExpressionSet(exprs);
    }

    /**
     * Returns the LatticeElement of eval[node]
     */
     public AvailableExpressionSet eval(CFGNode node) {
         assert node instanceof CFGNodeIR;
         IRStmt stmt = ((CFGNodeIR) node).getStatement();
         assert stmt != null;

         Set<IRExpr> set = new HashSet<>();
         // Always lowered IR
         if (stmt instanceof IRMove) {
             IRMove move = (IRMove)stmt;

             set.addAll(findReferences(subexprs(move.expr())));

             // [e1] = [e2]
             if (move.target() instanceof IRMem) {
                 set.addAll(findReferences(subexprs(move.target())));
             }
         }
         if (stmt instanceof IRCJump) {
             IRCJump cjump = (IRCJump) stmt;

             // if (e)
             set.addAll(findReferences(subexprs(cjump.expr())));
         }

         Set<TaggedExpression> exprs = new HashSet<>();
         AvailableExpressionSet inSet = (AvailableExpressionSet) node.getIn();
         for (IRExpr expr : set) {
             TaggedExpression pair = inSet.get(expr);
             if (pair == null) {
                 exprs.add(new TaggedExpression(expr, (CFGNodeIR) node));
             } else {
                 exprs.add(pair);
             }
         }

         return new AvailableExpressionSet(exprs);

     }

    /**
     * Removes kill[node] from the set out
     */
    public void kill(CFGNode node, Set<TaggedExpression> out) {
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
        for (Iterator<TaggedExpression> i = out.iterator(); i.hasNext();) {
            TaggedExpression pair = i.next();
            // remove if expr contains any usage of x
            if (containsCondition(pair.expr, filter)) {
                i.remove();
            }
        }
    }

    // Returns a set of expressions that are subexpressions of expr
    // Analogous to powerset
    private static Set<IRExpr> subexprs(IRExpr expr) {
        HashSet<IRExpr> set = new HashSet<>();
        addSubexprs(expr, set);
        return set;
    }

    /**
     * Adds all nontrivial subexpressions of expr to the set
     */
    private static void addSubexprs(IRExpr expr, Set<IRExpr> set) {
        // We don't add a subexpression unless it involves some sort of
        // computation, so no CONST or TEMP
        if (expr instanceof IRBinOp) {
            set.add(expr);
            IRBinOp binop = (IRBinOp)expr;
            addSubexprs(binop.left(), set);
            addSubexprs(binop.right(), set);
        }
        if (expr instanceof IRCall) {
            // We don't add the call itself to the set of subexpressions because
            // it could return different results
            // TODO: Check if we need to do this
            IRCall call = (IRCall)expr;
            for (IRExpr e : call.args()) {
                addSubexprs(e, set);
            }
        }
        if (expr instanceof IRMem) {
            set.add(expr);
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

    /**
     * Helper function that returns if two IRExprs are structurally equal
     */
    public static boolean exprEquals(IRExpr e1, IRExpr e2) {
        if (!e1.getClass().equals(e2.getClass())) {
            return false;
        }

        if (e1 instanceof IRBinOp) {
            assert e2 instanceof IRBinOp;

            IRBinOp e1_ = (IRBinOp)e1;
            IRBinOp e2_ = (IRBinOp)e2;

            return e1_.opType().equals(e2_.opType()) &&
                    exprEquals(e1_.left(), e2_.left()) &&
                    exprEquals(e1_.right(), e2_.right());
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
        assert allExprs != null;

        for (IRExpr e2 : allExprs) {
            if (exprEquals(e1, e2)) {
                return e2;
            }
        }
        System.out.println(e1);
        throw new RuntimeException("Expression not found in top for " +
                "available expressions analysis. Please email esl89@cornell.edu");
    }

    /*
        Same helper as above, but does it for a set of IRExprs
     */
    public Set<IRExpr> findReferences(Set<IRExpr> exprs) {
        Set<IRExpr> refs = new HashSet<>();
        for (IRExpr e : exprs) {
            refs.add(findReference(e));
        }

        return refs;
    }
}
