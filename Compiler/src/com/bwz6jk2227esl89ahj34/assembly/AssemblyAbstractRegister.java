package com.bwz6jk2227esl89ahj34.assembly;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import lombok.Data;

import java.util.HashMap;

@Data
public class AssemblyAbstractRegister extends AssemblyRegister {
    // Maintains the number of temps that aren't return or argument temps
    public static int counter = 0;
    // Keeps track of id's that were assigned to given temp names
    private static HashMap<String, Integer> nameIdMap = new HashMap<>();

    // The id for this instance
    public int id;

    /**
     * Resets the counter to 0 and nameIdMap to a new HashMap
     */
    public static void reset() {
        counter = 0;
        nameIdMap = new HashMap<>();
    }

    public AssemblyAbstractRegister() {
        id = counter++;
    }

    /**
     * Returns a new id if the given temp's name has not been encountered before. Otherwise,
     * return the id stored in the map.
     * If temp is a return temp, mark the abstract register as a return temp
     * If temp is an argument temp, mark the abstract register as an argument temp
     */
    public AssemblyAbstractRegister(IRTemp temp) {
        String name = temp.name();
        Integer registerId = nameIdMap.get(name);
        if (registerId != null) {
            id = registerId;
            return;
        }
        id = counter++;
        nameIdMap.put(name, id);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AssemblyAbstractRegister)) {
            return false;
        }
        AssemblyAbstractRegister register = (AssemblyAbstractRegister) object;
        return this.id == register.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        String s = "%";
        return s + id;
    }
}
