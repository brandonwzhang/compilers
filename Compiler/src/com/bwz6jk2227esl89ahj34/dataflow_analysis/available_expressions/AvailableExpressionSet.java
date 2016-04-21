package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data

public class AvailableExpressionSet extends LatticeElement{
    private Set<IRExpr> exprs;

    public AvailableExpressionSet(Set<IRExpr> set) {
        this.exprs = new HashSet<IRExpr>(set);
    }
}
