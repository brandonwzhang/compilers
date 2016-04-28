package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNodeIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
public class AvailableExpressionSet extends LatticeElement {
    public static class ExpressionNodePair {
        public IRExpr expr;
        public CFGNodeIR node;

        public ExpressionNodePair(IRExpr expr, CFGNodeIR node) {
            this.expr = expr;
            this.node = node;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ExpressionNodePair)) {
                return false;
            }
            ExpressionNodePair pair = (ExpressionNodePair) o;
            return expr.equals(pair.expr);
        }

        @Override
        public int hashCode() {
            return expr.hashCode();
        }
    }
    private Set<ExpressionNodePair> pairs;

    public AvailableExpressionSet(Set<ExpressionNodePair> set) {
        this.pairs = new HashSet<>(set);
    }

    public ExpressionNodePair get(IRExpr expr) {
        for (ExpressionNodePair pair : pairs) {
            if (expr.equals(pair.expr)) {
                return pair;
            }
        }
        return null;
    }

    public LatticeElement copy() {
        Set<ExpressionNodePair> newPairs = new HashSet<>();
        newPairs.addAll(pairs);
        return new AvailableExpressionSet(newPairs);
    }

    public boolean equals(LatticeElement element) {
        if (!(element instanceof AvailableExpressionSet)) {
            return false;
        } else {
            return ((AvailableExpressionSet)element).getPairs().equals(pairs);
        }
    }


    public String toString() {
        return pairs.toString().replace('\n', ' ');
    }

}
