package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;

import java.util.HashMap;

public class AssemblyAbstractRegister extends AssemblyExpression {
    // Maintains the next id to be assigned to a new register
    private static int curId = 0;
    // Keeps track of id's that were assigned to given names
    private static HashMap<String, Integer> nameIdMap = new HashMap<>();

    // The id for this instance
    public int id;

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
}
