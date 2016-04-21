package com.bwz6jk2227esl89ahj34.dataflow_analysis.live_vars;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import lombok.Data;

import java.util.Set;
@Data
public class LiveVariableSet extends LatticeElement{
    private Set<AssemblyAbstractRegister> liveVars;

    public LiveVariableSet(Set<AssemblyAbstractRegister> set) {
        this.liveVars = set;
    }

}
