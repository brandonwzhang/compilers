package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AvailableCopiesSet extends LatticeElement {
    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    public static class TempPair {
        public String left;
        public String right;
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TempPair)) {
                return false;
            }
            TempPair tempPair = (TempPair) o;
            return left.equals(tempPair.left) && right.equals(tempPair.right);
        }
        @Override
        public int hashCode() {
            return left.hashCode() + right.hashCode();
        }
    }

    private Set<TempPair> copies;

    /**
     * Returns the mapping for a given temp name.
     * Returns null if no mapping exists
     */
    public String get(String tempName) {
        String mapping = null;
        for (TempPair tempPair : copies) {
            if (tempPair.left.equals(tempName)) {
                assert mapping == null : "More than one mapping for temp";
                mapping = tempPair.right;
            }
        }
        return mapping;
    }

    @Override
    public String toString() {
        String s = "";
        for (TempPair pair : copies) {
            s += pair.left + "->" + pair.right + "\n";
        }
        return s;
    }

    @Override
    public LatticeElement copy() {
        Set<TempPair> newSet = new HashSet<>(copies);
        return new AvailableCopiesSet(newSet);
    }

    @Override
    public boolean equals(LatticeElement set) {
        if (!(set instanceof AvailableCopiesSet)) {
            return false;
        }
        return copies.equals(((AvailableCopiesSet)set).getCopies());
    }

    public void intersect(LatticeElement set) {
        assert set instanceof AvailableCopiesSet;
        // then we take information that is common in both
        AvailableCopiesSet castedSet = (AvailableCopiesSet) set;
        Set<TempPair> castedSetCopies = new HashSet<>(castedSet.getCopies());
        copies.retainAll(castedSetCopies);
    }

    public AvailableCopiesSet subtract(AvailableCopiesSet subtrahend) {
        Set<TempPair> resultCopies = new HashSet<>(copies);
        Set<TempPair> subtrahendCopies = subtrahend.getCopies();
        resultCopies.removeAll(subtrahendCopies);
        return new AvailableCopiesSet(resultCopies);
    }

    public AvailableCopiesSet union(AvailableCopiesSet set) {
        Set<TempPair> unionCopies = new HashSet<>(copies);
        Set<TempPair> setCopies = set.getCopies();
        unionCopies.addAll(setCopies);
        return new AvailableCopiesSet(unionCopies);
    }
}
