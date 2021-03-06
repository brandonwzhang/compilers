package com.bwz6jk2227esl89ahj34.analysis;

public class LatticeBottom extends LatticeElement {

    @Override
    public LatticeElement copy() {
        return new LatticeBottom();
    }

    @Override
    public boolean equals(LatticeElement element) {
        return element instanceof LatticeBottom;
    }

    public String toString() { return "BOTTOM"; }
}
