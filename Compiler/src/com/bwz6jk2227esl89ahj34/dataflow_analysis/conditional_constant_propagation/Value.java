package com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation;


import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.ir.IRConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class Value extends LatticeElement {
    private IRConst value;

    @Override
    public boolean equals(Object o) {
        assert o instanceof Value;
        Value castedO = (Value)o;
        return this.getValue().value() == castedO.getValue().value();
    }

    @Override
    public boolean equals(LatticeElement e) {
        return this.equals((Object)e);
    }

    @Override
    public LatticeElement copy() {
        return new Value(new IRConst(value.value()));
    }


}
