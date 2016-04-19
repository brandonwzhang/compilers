package com.bwz6jk2227esl89ahj34.optimization.live_vars;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode;
import com.bwz6jk2227esl89ahj34.optimization.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;

import java.util.Set;

public class LiveVariableAnalysis extends DataflowAnalysis{

    private Direction direction = Direction.BACKWARD;
    private CFGNode<AssemblyInstruction> startNode;

    public LatticeElement transfer(LatticeElement element) {
        // TODO
        return null;
    }
    public LatticeElement meet(Set<LatticeElement> elements) {
        // TODO
        return null;
    }
}
