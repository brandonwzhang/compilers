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

    public AvailableExpressionsAnalysis(IRSeq stmt) {
        super(stmt, Direction.FORWARD); //temp; fix if needed -- jihun
    }
    private Set<IRExpr> subexprs(IRExpr expr) {
        // TODO
        return null;
    }

    public AvailableExpressionSet eval(CFGNode node) {
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

    public void kill(CFGNode node, Set<IRExpr> out) {
        // destructively modify out to remove kill[node]
        IRStmt stmt = ((CFGNodeIR)node).getStatement();
        assert stmt != null;

        if (stmt instanceof IRMove) {
            IRMove move = (IRMove) stmt;

            // x = f(e...)
            if (move.expr() instanceof IRCall) {
                // TODO
                // PANIC MODE: Kill everything

                // normal mode: remove [e'] that can be changed by call to f
            }
            // x = e
            if (move.target() instanceof IRTemp){
                IRTemp x = (IRTemp)move.expr();
                for(Iterator<IRExpr> i = out.iterator(); i.hasNext();) {
                    IRExpr expr = i.next();
                    // remove if expr contains any usage of x
                    if (containsCondition(expr, e -> (e instanceof IRTemp) ? ((IRTemp)e).name().equals(x.name()) : false)) {
                        i.remove();
                    }
                }
            }

            // [e1] = [e2]
            if (move.target() instanceof IRMem) {
                // TODO
                // PANIC MODE: Kill everything
            }
        }
    }

    private boolean containsCondition(IRExpr expr, Predicate filter) {
        if (expr instanceof IRCall) {
            for (IRExpr e : ((IRCall)expr).args()) {
                if (containsCondition(e, filter)) {
                    return true;
                }
            }
            return false;
        }
        if (expr instanceof IRMem) {
            return containsCondition(((IRMem)expr).expr(), filter);
        }
        if (expr instanceof IRBinOp) {
            IRBinOp binop = (IRBinOp)expr;
            return containsCondition(binop.left(), filter) || containsCondition(binop.right(), filter);
        }
        return filter.test(expr);
    }


    public void transfer(CFGNode node) {
        // in[n] = intersection over all out[n'], where n' is pred of n
        Set<LatticeElement> pred_outs = new HashSet<>();
        for (CFGNode pred : node.getPredecessors()) {
            pred_outs.add(pred.getOut());
        }
        node.setIn(meet(pred_outs));

        // out[n] = in[n] U eval[n] - kill[n]
        Set<IRExpr> out = new HashSet<IRExpr>(((AvailableExpressionSet)node.getIn()).getExprs());
        out.addAll(eval(node).getExprs());
        //out.removeAll(kill(node).getExprs());
        kill(node, out);
        node.setOut(new AvailableExpressionSet(out));
    }


    public LatticeElement meet(Set<LatticeElement> elements) {
        assert elements.size() >= 2;

        // start with Top
        LatticeElement meet = new LatticeTop();

        // intersect over all elements
        for (LatticeElement element : elements) {
            if (element instanceof LatticeBottom) {
                return element; // return bottom, can't get less informative
            }
            if (element instanceof LatticeTop) {
                continue; // Top = {all exprs}
            }

            // now element must be type AvailableExpressionSet
            if (meet instanceof LatticeTop) {
                meet = new AvailableExpressionSet(((AvailableExpressionSet)element).getExprs());
            }
            else {
                // intersect
                ((AvailableExpressionSet)meet).getExprs().retainAll(((AvailableExpressionSet)element).getExprs());
            }
        }

        return meet;
    }

}
