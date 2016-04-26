package com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation;


import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
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
    private Map<String, LatticeElement>  valueTuples;

    @Override
    public String toString() {
        String s = "Unreachable ->"+unreachable+"\n";
        for (String temp : valueTuples.keySet()) {
            if (!(valueTuples.get(temp) instanceof LatticeTop)) {
                s += temp + " -> " + valueTuples.get(temp).toString() + "\n";
            }
        }
        return s;
    }
    public UnreachableValueTuplesPair(List<IRTemp> temps) {
        this.valueTuples = new HashMap<>();
        for(IRTemp temp : temps) {
            if (temp.name().contains(Configuration.ABSTRACT_ARG_PREFIX)) {
                this.valueTuples.put(temp.name(), new LatticeBottom());
            } else {
                this.valueTuples.put(temp.name(), new LatticeTop());
            }
        }
        unreachable = true;
    }

    @Override
    public LatticeElement copy() {
        Map<String, LatticeElement> valueTuplesCopy = new HashMap<>();
        for (String key : valueTuples.keySet()) {
            if(valueTuples.get(key) instanceof LatticeBottom) {
                valueTuplesCopy.put(key, new LatticeBottom());
            } else if (valueTuples.get(key) instanceof LatticeTop) {
                valueTuplesCopy.put(key, new LatticeTop());
            } else {
                Value value = (Value)(((Value) (valueTuples.get(key))).copy());
                valueTuplesCopy.put(key, value);
            }
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

            Map<String, LatticeElement> castedElementMap = castedElement.getValueTuples();
            if (castedElementMap.keySet().size() != valueTuples.keySet().size()) {
                return false;
            }

            if (unreachable != castedElement.isUnreachable()) {
                return false;
            }

            for (String key : castedElementMap.keySet()) {
                if (!valueTuples.containsKey(key)) {
                    return false;
                }
                if (!castedElementMap.get(key).equals(valueTuples.get(key))) {
                    return false;
                }
            }
            return true;
        }
    }
}
