package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jihunkim on 4/20/16.
 */
@Data
@AllArgsConstructor
public class AvailableCopiesSet extends LatticeElement {
    private Map<IRTemp, IRTemp> map;

    @Override
    public LatticeElement copy() {
        Map<IRTemp, IRTemp> newMap = new HashMap<>();
        newMap.putAll(map);
        return new AvailableCopiesSet(newMap);
    }

    @Override
    public boolean equals(LatticeElement set) {
        if (!(set instanceof AvailableCopiesSet)) {
            return false;
        } else {
            return map.equals(((AvailableCopiesSet)set).getMap());
        }
    }

    public void intersect(LatticeElement set) {
        if (set instanceof LatticeTop) {
            // then map stays the same
        } else if (set instanceof LatticeBottom) {
            // then we empty out map
            map = new HashMap<>();
        } else if (set instanceof AvailableCopiesSet) {
            // then we take information that is common in both
            AvailableCopiesSet castedSet = (AvailableCopiesSet) set;
            Map<IRTemp, IRTemp> castedSetMap = castedSet.getMap();
            Map<IRTemp, IRTemp> newMap = new HashMap<>();
            for (IRTemp key : map.keySet()) {
                if (castedSetMap.containsKey(key) &&
                        castedSetMap.get(key).name().equals(map.get(key).name())) {
                    newMap.put(key, map.get(key));
                }
            }
            map = newMap;
        }
    }

    public AvailableCopiesSet subtract(AvailableCopiesSet subtrahend) {
        AvailableCopiesSet copy = (AvailableCopiesSet) this.copy();
        Map<IRTemp, IRTemp> subtrahendMap = subtrahend.getMap();
        for (IRTemp key : subtrahendMap.keySet()) {
            if (copy.getMap().containsKey(key) &&
                    copy.getMap().get(key).name().equals(subtrahendMap.get(key).name())) {
                  copy.getMap().remove(key);
            }
        }
        return copy;
    }

    public AvailableCopiesSet union(AvailableCopiesSet set) {
        Map<IRTemp, IRTemp> union = new HashMap<>();
        union.putAll(map);
        union.putAll(set.getMap());
        return new AvailableCopiesSet(union);
    }
}
