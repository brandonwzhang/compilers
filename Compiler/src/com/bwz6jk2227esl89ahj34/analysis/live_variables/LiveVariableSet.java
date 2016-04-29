package com.bwz6jk2227esl89ahj34.analysis.live_variables;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;

import com.bwz6jk2227esl89ahj34.analysis.LatticeElement;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class LiveVariableSet extends LatticeElement{
    private Set<AssemblyAbstractRegister> liveVars;

    public LiveVariableSet(Set<AssemblyAbstractRegister> set) {
        this.liveVars = set;
    }

    @Override
    public LatticeElement copy() {
        return new LiveVariableSet(new LinkedHashSet<>(liveVars));
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
