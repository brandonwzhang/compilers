package com.bwz6jk2227esl89ahj34.optimization.available_expressions;

import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import com.bwz6jk2227esl89ahj34.ir.IRNode;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode;
import com.bwz6jk2227esl89ahj34.optimization.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;

import java.util.HashSet;
import java.util.Set;

public class AvailableExpressionsAnalysis extends DataflowAnalysis{
    private Direction direction = Direction.FORWARD;
    private CFGNode<IRNode> startNode;


    public void transfer(LatticeElement element) {
        // TODO
    }

    public AvailableExpressionSet meet(Set<LatticeElement> elements) {
        // start with Top
        Set<IRExpr> intersect = new HashSet<>();
        boolean b = true;

        // if first element, then take all the elements. else intersect.
        for (LatticeElement element : elements) {
            if(b) {
                intersect.addAll(((AvailableExpressionSet)element).getExprs());
                b = false;
            }
            intersect.retainAll(((AvailableExpressionSet)element).getExprs());
        }

        return new AvailableExpressionSet(intersect);
    }

}
