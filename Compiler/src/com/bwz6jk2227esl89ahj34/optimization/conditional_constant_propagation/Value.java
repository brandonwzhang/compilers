package com.bwz6jk2227esl89ahj34.optimization.conditional_constant_propagation;

import com.bwz6jk2227esl89ahj34.ir.IRConst;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import lombok.Data;

/**
 * Created by jihunkim on 4/19/16.
 */
@Data

public class Value extends LatticeElement {
    private IRConst value;

    @Override
    public boolean equals(Object o) {
        assert o instanceof Value;
        Value castedO = (Value)o;
        return this.getValue().value() == castedO.getValue().value();
    }
}
