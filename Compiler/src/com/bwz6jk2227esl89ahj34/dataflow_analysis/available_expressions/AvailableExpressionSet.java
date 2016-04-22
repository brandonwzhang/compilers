package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
public class AvailableExpressionSet extends LatticeElement {
    private Set<IRExpr> exprs;

    public AvailableExpressionSet(Set<IRExpr> set) {
        this.exprs = new HashSet<IRExpr>(set);
    }

    // TODO
    /*
        Keep a hashset of known exprs
        When creating one, see if it's used in the hashset (structural equality)
        If it is, use that reference in the hashset (discarding newly created expr)
        If not, put it in
     */

    public LatticeElement copy() {
        Set<IRExpr> newExprs = new HashSet<>();
        newExprs.addAll(exprs);
        return new AvailableExpressionSet(newExprs);
    }

    // checks structural equality
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
