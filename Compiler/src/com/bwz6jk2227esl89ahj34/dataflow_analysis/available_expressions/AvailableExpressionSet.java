package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNodeIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class AvailableExpressionSet extends LatticeElement {
    public static class TaggedExpression {
        public IRExpr expr;
        public CFGNodeIR node;

        public TaggedExpression(IRExpr expr, CFGNodeIR node) {
            this.expr = expr;
            this.node = node;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TaggedExpression)) {
                return false;
            }
            TaggedExpression pair = (TaggedExpression) o;
            return expr.equals(pair.expr);
        }

        @Override
        public int hashCode() {
            return expr.hashCode();
        }
    }
    private Set<TaggedExpression> exprs;

    public AvailableExpressionSet(Set<TaggedExpression> set) {
        this.exprs = new LinkedHashSet<>(set);
    }

    public TaggedExpression get(IRExpr expr) {
        for (TaggedExpression pair : exprs) {
            if (expr.equals(pair.expr)) {
                return pair;
            }
        }
        return null;
    }

    public LatticeElement copy() {
        Set<TaggedExpression> newPairs = new LinkedHashSet<>();
        newPairs.addAll(exprs);
        return new AvailableExpressionSet(newPairs);
    }

    public boolean equals(LatticeElement element) {
        if (!(element instanceof AvailableExpressionSet)) {
            return false;
        } else {
            return ((AvailableExpressionSet)element).getExprs().equals(exprs);
        }
    }


    public String toString() {
        return exprs.toString().replace('\n', ' ');
    }

}
