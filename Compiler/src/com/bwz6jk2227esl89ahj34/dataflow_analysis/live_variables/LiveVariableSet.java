package com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
@Data
public class LiveVariableSet extends LatticeElement{
    private Set<AssemblyAbstractRegister> liveVars;

    public LiveVariableSet(Set<AssemblyAbstractRegister> set) {
        this.liveVars = set;
    }

    @Override
    public LatticeElement copy() {
        return new LiveVariableSet(new HashSet<>(liveVars));
    }

    @Override
    public boolean equals(LatticeElement element) {
        if (!(element instanceof LiveVariableSet)) {
            return false;
        }
        LiveVariableSet vars = (LiveVariableSet) element;
        return liveVars.equals(vars.getLiveVars());
    }

    @Override
    public String toString() {
        return liveVars.toString();
    }

}
