package com.bwz6jk2227esl89ahj34.optimization.conditional_constant_propagation;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import com.bwz6jk2227esl89ahj34.optimization.LatticeTop;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jihunkim on 4/19/16.
 */
@Data
@AllArgsConstructor
public class UnreachableValueTuplesPair extends LatticeElement {
    private boolean unreachable;
    private Map<IRTemp, LatticeElement>  valueTuples;

    public UnreachableValueTuplesPair(List<IRTemp> temps) {
        this.valueTuples = new HashMap<>();
        for(IRTemp temp : temps) {
            this.valueTuples.put(temp, new LatticeTop());
        }
        unreachable = true;
    }

}
