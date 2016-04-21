package com.bwz6jk2227esl89ahj34.dataflow_analysis;

public abstract class LatticeElement {
    /**
     * Return true iff this element is equal to the given element by the partial order
     */
    public abstract boolean equals(LatticeElement element);

    /**
     * Returns a deep copy of this LatticeElement
     */
    public abstract LatticeElement copy();
}
