package com.bwz6jk2227esl89ahj34.dataflow_analysis;

public class LatticeBottom extends LatticeElement {

    public LatticeElement copy() {
        return new LatticeBottom();
    }

    @Override
    public boolean equals(LatticeElement element) {
        return element instanceof LatticeBottom;
    }
}
