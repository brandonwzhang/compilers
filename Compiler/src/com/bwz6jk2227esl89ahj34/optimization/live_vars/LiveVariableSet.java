package com.bwz6jk2227esl89ahj34.optimization.live_vars;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import lombok.Data;

import java.util.Set;
@Data
public class LiveVariableSet extends LatticeElement{
    private Set<AssemblyAbstractRegister> liveVars;

    public LiveVariableSet(Set<AssemblyAbstractRegister> set) {
        this.liveVars = set;
    }

}