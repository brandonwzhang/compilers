package com.bwz6jk2227esl89ahj34.optimization.conditional_constant_propagation;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.optimization.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by jihunkim on 4/19/16.
 */
public class ConditionalConstantPropagation extends DataflowAnalysis {


    public void transfer(CFGNode element) {

    }

    //TODO: add assert statements and spec
    public UnreachableValueTuplesPair meet(Set<LatticeElement> elements) {
        assert elements.size() >= 2;
        Iterator<LatticeElement> iterator = elements.iterator();
        UnreachableValueTuplesPair accumulator = (UnreachableValueTuplesPair) iterator.next();
        while(iterator.hasNext()) {
            UnreachableValueTuplesPair next = (UnreachableValueTuplesPair) iterator.next();
            accumulator.setUnreachable(accumulator.isUnreachable()
                    && next.isUnreachable());
            Map<IRTemp, LatticeElement> accumulatorMap = accumulator.getValueTuples();
            Map<IRTemp, LatticeElement> nextMap = accumulator.getValueTuples();
            for (IRTemp key : accumulatorMap.keySet()) {
                accumulatorMap.put(key,
                        valueMeet(accumulatorMap.get(key), nextMap.get(key)));
            }
            accumulator.setValueTuples(accumulatorMap);
        }
        return accumulator;
    }

    public LatticeElement valueMeet(LatticeElement e1, LatticeElement e2) {
        if(e1 instanceof LatticeBottom || e2 instanceof LatticeBottom) {
            return new LatticeBottom();
        } else if (e1 instanceof LatticeTop) {
            return e2;
        } else if (e2 instanceof LatticeTop) {
            return e1;
        } else {
            assert e1 instanceof Value && e2 instanceof Value;
            if (!((Value)e1).equals((Value)e2)) {
                return new LatticeBottom();
            } else {
                return e1;
            }
        }
    }

}
