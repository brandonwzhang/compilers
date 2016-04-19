package com.bwz6jk2227esl89ahj34.optimization;

import java.util.Set;

public abstract class DataflowAnalysis {


    public abstract LatticeElement meet(Set<LatticeElement> elements);
}
