package com.bwz6jk2227esl89ahj34.dataflow_analysis;

public class LatticeTop extends LatticeElement {

    @Override
    public LatticeElement copy() {
        return new LatticeTop();
    }

    @Override
    public boolean equals(LatticeElement element) {
        return element instanceof LatticeTop;
    }

    @Override
    public String toString() { return "TOP"; }
}
