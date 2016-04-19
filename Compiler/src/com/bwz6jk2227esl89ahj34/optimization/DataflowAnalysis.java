package com.bwz6jk2227esl89ahj34.optimization;

import java.util.Set;

public abstract class DataflowAnalysis {
    public enum Direction {
        FORWARD, BACKWARD
    }

    private Direction direction;
    private CFGNode startNode;

    public abstract void transfer(CFGNode node);

    public abstract LatticeElement meet(Set<LatticeElement> elements);
}
