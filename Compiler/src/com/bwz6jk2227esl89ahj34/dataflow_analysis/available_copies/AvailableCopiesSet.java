package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class AvailableCopiesSet extends LatticeElement {
    private Map<String, String> map;

    @Override
    public String toString() {
        String s = "";
        for (String key : map.keySet()) {
            s += key + "->" + map.get(key)+"\n";
        }
        return s;
    }

    @Override
    public LatticeElement copy() {
        Map<String, String> newMap = new HashMap<>();
        newMap.putAll(map);
        return new AvailableCopiesSet(newMap);
    }

    @Override
    public boolean equals(LatticeElement set) {
        if (!(set instanceof AvailableCopiesSet)) {
            return false;
        } else {
           // System.out.println("wat");
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
            Map<String, String> castedSetMap = castedSet.getMap();
            Map<String, String> newMap = new HashMap<>();
            for (String key : map.keySet()) {
                if (castedSetMap.containsKey(key) &&
                        castedSetMap.get(key).equals(map.get(key))) {
                    newMap.put(key, map.get(key));
                }
            }
            map = newMap;
        }
    }

    public AvailableCopiesSet subtract(AvailableCopiesSet subtrahend) {
        AvailableCopiesSet copy = (AvailableCopiesSet) this.copy();
        Map<String, String> subtrahendMap = subtrahend.getMap();
        for (String key : subtrahendMap.keySet()) {
            if (copy.getMap().containsKey(key) &&
                    copy.getMap().get(key).equals(subtrahendMap.get(key))) {
                  copy.getMap().remove(key);
            }
        }
        return copy;
    }

    public AvailableCopiesSet union(AvailableCopiesSet set) {
        Map<String, String> union = new HashMap<>();
        union.putAll(map);
        union.putAll(set.getMap());
        return new AvailableCopiesSet(union);
    }
}
