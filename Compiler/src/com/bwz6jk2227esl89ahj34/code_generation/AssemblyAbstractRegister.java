package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Data
@EqualsAndHashCode
public class AssemblyAbstractRegister extends AssemblyRegister {
    // Maintains the next id to be assigned to a new register
    private static int curId = 0;
    // Keeps track of id's that were assigned to given temp names
    private static HashMap<String, Integer> nameIdMap = new HashMap<>();

    // The id for this instance
    public int id;

    /**
     * Resets the curId to 0 and nameIdMap to a new HashMap
     */
    public static void reset() {
        curId = 0;
        nameIdMap = new HashMap<>();
    }

    public static int getCurId() { return curId; }

    public AssemblyAbstractRegister() {
        id = curId++;
    }

    /**
     * Returns a new id if the given temp's name has not been encountered before. Otherwise,
     * return the id stored in the map.
     */
    public AssemblyAbstractRegister(IRTemp temp) {
        String name = temp.name();
        Integer registerId = nameIdMap.get(name);
        if (registerId != null) {
            id = registerId;
            return;
        }
        id = curId++;
        nameIdMap.put(name, id);
    }

    @Override
    public String toString() {
        return "%a" + id;
    }
}
