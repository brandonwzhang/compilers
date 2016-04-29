package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions
        .AvailableExpressionSet.TaggedExpression;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions.AvailableExpressions;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions.AvailableExpressions.ExpressionNodePair;
import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CommonSubexpressionVisitor extends IRVisitor {
    // The set of available expressions available at the statement we're visiting
    private AvailableExpressionSet set;
    // Analysis used to find available expressions
    // We need this to find the references of equivalent expressions
    private AvailableExpressions analysis;
    // A mapping from IRExpr's to IRTemp's that we use to replace common subexpressions
    private Map<IRExpr, IRTemp> map;
    // A set of expression that are redundant enough to be replaced
    private Set<ExpressionNodePair> redundantSubexpressions;
    // Keep track of the CSE variables we use in this statement
    public Set<ExpressionNodePair> usedTemps = new LinkedHashSet<>();

    public CommonSubexpressionVisitor(AvailableExpressionSet set,
                                      AvailableExpressions analysis,
                                      Map<IRExpr, IRTemp> map,
                                      Set<ExpressionNodePair> redundantSubexpressions) {
        this.set = set;
        this.analysis = analysis;
        this.map = map;
        this.redundantSubexpressions = redundantSubexpressions;
    }

    protected IRVisitor enter(IRNode parent, IRNode n) {
        ExpressionNodePair mapping = getNodeMapping(n);
        if (mapping != null) {
            // If we match a CSE temp here, we pass a dummy visitor to children, so we don't
            // anything in the subtree to usedTemps
            usedTemps.add(mapping);
            return new CommonSubexpressionVisitor(set, analysis, map, redundantSubexpressions);
        }
        return this;
    }

    /**
     * Replaces an instance of a common subexpression with a
     */
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        ExpressionNodePair mapping = getNodeMapping(n);
        if (mapping == null) {
            return n_;
        }
        return mapping.expr;
    }

    private ExpressionNodePair getNodeMapping(IRNode n) {
        if (!(n instanceof IRExpr) || n instanceof IRConst || n instanceof IRTemp || n instanceof IRCall || n instanceof IRName) {
            // If it's not an IRExpr, we just return the result of visiting the children
            return null;
        }
        // Get the reference to an equivalent expr so we can compare physical equality
        IRExpr expr = analysis.findReference((IRExpr) n);

        TaggedExpression taggedExpr = set.get(expr);

        if (taggedExpr == null) {
            // If the expr is not available, we just return the result of visiting the children
            return null;
        }

        if (!redundantSubexpressions.contains(new ExpressionNodePair(taggedExpr))) {
            // This expression is not redundant enough to be replaced
            return null;
        }

        IRTemp mapping = map.get(expr);
        // There should be a mapping for every expression
        assert mapping != null;

        return new ExpressionNodePair(mapping, taggedExpr.node);
    }
}
