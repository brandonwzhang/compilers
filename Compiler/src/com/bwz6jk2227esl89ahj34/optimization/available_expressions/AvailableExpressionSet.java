package com.bwz6jk2227esl89ahj34.optimization.available_expressions;

import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import lombok.Data;

import java.util.Set;
@Data

public class AvailableExpressionSet extends LatticeElement{
    private Set<IRExpr> exprs;

    public AvailableExpressionSet(Set<IRExpr> set) {
        this.exprs = set;
    }
}
