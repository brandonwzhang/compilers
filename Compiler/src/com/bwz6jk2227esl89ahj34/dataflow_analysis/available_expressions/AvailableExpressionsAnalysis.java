package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class AvailableExpressionsAnalysis extends DataflowAnalysis{
    private Direction direction = Direction.FORWARD;
    private CFGNode startNode;

    public AvailableExpressionsAnalysis(IRSeq seq) {
        super(seq, Direction.FORWARD, allExprs(seq), new AvailableExpressionSet(new HashSet<IRExpr>()));
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

        AvailableExpressionSet meet = (AvailableExpressionSet)top.copy();
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
                     set.add(e);
                     set.addAll(subexprs(e));
                 }
             } else { // x = e
                 set.add(move.expr());
                 set.addAll(subexprs(move.expr()));
             }

             // [e1] = [e2]
             if (move.target() instanceof IRMem) {
                 set.add(move.target());
                 set.addAll(subexprs(move.target()));
             }
         }
         if (stmt instanceof IRCJump) {
             IRCJump cjump = (IRCJump) stmt;

             // if (e)
             set.add(cjump.expr());
             set.addAll(subexprs(cjump.expr()));
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

        if (stmt instanceof IRMove) {
            IRMove move = (IRMove) stmt;

            // x = e
            if (move.target() instanceof IRTemp){
                IRTemp x = (IRTemp)move.target();
                for (Iterator<IRExpr> i = out.iterator(); i.hasNext();) {
                    IRExpr expr = i.next();
                    // remove if expr contains any usage of x
                    if (containsCondition(expr, e -> (e instanceof IRTemp) ? ((IRTemp)e).name().equals(x.name()) : false)) {
                        i.remove();
                    }
                }
            }

            // [e1] = e or x = f(e...)
            // Idea: Remove any expression with an [e'] where e' can alias e1
            // Idea: Remove any expression with an [e'] where f can affect [e']
            if (move.target() instanceof IRMem || move.expr() instanceof IRCall) {
                // PANIC MODE: Kill everything

                // normal mode: kill any expression with [e'] where e' can alias e1
                for (Iterator<IRExpr> i = out.iterator(); i.hasNext();) {
                    IRExpr expr = i.next();
                    // remove if expr contains potential alias

                    // For the project's sake, we will remove any expression
                    // that has an IRMem at all
                    if (containsCondition(expr, e -> e instanceof IRMem)) {
                        i.remove();
                    }
                }

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
                addSubexprs(move.target(), set); // TODO: should we have this?
                addSubexprs(move.expr(), set);
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


}
