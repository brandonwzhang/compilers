package com.bwz6jk2227esl89ahj34.optimization.live_vars;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode;
import com.bwz6jk2227esl89ahj34.optimization.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;

import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis extends DataflowAnalysis{

    private Direction direction = Direction.BACKWARD;
    private CFGNode startNode;

    public void transfer(CFGNode node) {
        // TODO
    }

    public LiveVariableSet meet(Set<LatticeElement> elements) {
        Set<AssemblyAbstractRegister> union = new HashSet<>();
        for (LatticeElement element : elements) {
            union.addAll(((LiveVariableSet)element).getLiveVars());
        }
        return new LiveVariableSet(union);
    }
}
