package com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation;


import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UnreachableValueTuplesPair extends LatticeElement {
    private boolean unreachable;
    private Map<IRTemp, LatticeElement>  valueTuples;

    @Override
    public String toString() {
        String s = "Unreachable ->"+unreachable+"\n";
        for (IRTemp temp : valueTuples.keySet()) {
            s += temp.name() + " -> "+ valueTuples.get(temp).toString()+"\n";
        }
        return s;
    }
    public UnreachableValueTuplesPair(List<IRTemp> temps) {
        this.valueTuples = new HashMap<>();
        for(IRTemp temp : temps) {
            this.valueTuples.put(temp, new LatticeTop());
        }
        unreachable = true;
    }

    @Override
    public LatticeElement copy() {
        Map<IRTemp, LatticeElement> valueTuplesCopy = new HashMap<>();
        for (IRTemp key : valueTuples.keySet()) {
            valueTuplesCopy.put(key, valueTuples.get(key));
        }
        UnreachableValueTuplesPair copy =
                new UnreachableValueTuplesPair(unreachable, valueTuplesCopy);
        return copy;
    }

    @Override
    public boolean equals(LatticeElement element) {
        if (!(element instanceof UnreachableValueTuplesPair)) {
            return false;
        } else {
            UnreachableValueTuplesPair castedElement =
                    (UnreachableValueTuplesPair) element;
            return !(unreachable != castedElement.isUnreachable()) &&
                    !(castedElement.getValueTuples().equals(valueTuples));
        }
    }
}
