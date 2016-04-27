package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.ast.Expression;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionsAnalysis;


import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.Map;

public class CommonSubexpressionVisitor extends IRVisitor {
    // The set of available expressions available at the statement we're visiting
    private AvailableExpressionSet set;
    // Analysis used to find available expressions
    // We need this to find the references of equivalent expressions
    private AvailableExpressionsAnalysis analysis;
    // A mapping from IRExpr's to IRTemp's that we use to replace common subexpressions
    private Map<IRExpr, IRTemp> map;

    public CommonSubexpressionVisitor(AvailableExpressionSet set,
                                      AvailableExpressionsAnalysis analysis,
                                      Map<IRExpr, IRTemp> map) {
        this.set = set;
        this.analysis = analysis;
        this.map = map;
    }

    /**
     * Replaces an instance of a common subexpression with a
     */
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (!(n instanceof IRExpr) || n instanceof IRConst || n instanceof IRTemp || n instanceof IRCall || n instanceof IRName) {
            // If it's not an IRExpr, we just return the result of visiting the children
            return n_;
        }
        // Get the reference to an equivalent expr so we can compare physical equality
        IRExpr expr = analysis.findReference((IRExpr) n);

        assert analysis.allExprs.containsAll(set.getExprs());
        assert analysis.allExprs.contains(expr);

        if (!set.getExprs().contains(expr)) {
//            System.out.println("==================Set Expressions=================");
//            System.out.println(set.getExprs());
//            System.out.println("========================Expr======================");
//            System.out.println(expr);
//            System.out.println("**************************************************");
            // If the expr is not available, we just return the result of visiting the children
            return n_;
        }

        IRTemp mapping = map.get(expr);
        // There should be a mapping for every expression
        assert mapping != null;
        return mapping;
    }
}
