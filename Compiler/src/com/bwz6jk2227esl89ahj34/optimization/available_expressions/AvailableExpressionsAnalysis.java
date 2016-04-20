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
    private CFGNode startNode;

    public AvailableExpressionSet eval(CFGNode node) {
        // TODO

        return null;

    }
    public AvailableExpressionSet kill(CFGNode node) {
        // TODO
        return null;
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
        out.removeAll(kill(node).getExprs());
        node.setOut(new AvailableExpressionSet(out));
    }

    public AvailableExpressionSet meet(Set<LatticeElement> elements) {
        assert elements.size() >= 2;

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
