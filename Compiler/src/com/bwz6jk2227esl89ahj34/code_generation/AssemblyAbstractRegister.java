package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyAbstractRegister extends AssemblyRegister {
    // Maintains the number of temps that aren't return or argument temps
    public static int counter = 0;
    // Keeps track of id's that were assigned to given temp names
    private static HashMap<String, Integer> nameIdMap = new HashMap<>();

    // The id for this instance
    public int id;
    public boolean isArgument = false;
    public boolean isReturn = false;

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
        // If temp is an argument, we set the isArgument flag and set its id
        // to the argument temp number (e.g. _ARG0 would have id 0)
        int argumentTempNumber = getArgumentTempNumber(temp);
        if (argumentTempNumber >= 0) {
            isArgument = true;
            id = argumentTempNumber;
            return;
        }
        // If temp is register, we set the isReturn flag and set its id
        // to the return temp number (e.g. _RET0 would have id 0)
        int returnTempNumber = getReturnTempNumber(temp);
        if (returnTempNumber >= 0) {
            isReturn = true;
            id = returnTempNumber;
            return;
        }
        // We have a regular temp, so we just assign it an id or retrieve it from the map
        String name = temp.name();
        Integer registerId = nameIdMap.get(name);
        if (registerId != null) {
            id = registerId;
            return;
        }
        id = counter++;
        nameIdMap.put(name, id);
    }

    /**
     * Returns the number of the return temp. Returns -1 if not a return temp.
     */
    private static int getArgumentTempNumber(IRTemp temp) {
        String name = temp.name();
        if (name.length() < 5) {
            return -1;
        }
        if (name.substring(0, 4).equals(Configuration.ABSTRACT_ARG_PREFIX)) {
            return Integer.parseInt(name.substring(4));
        }
        return -1;
    }

    /**
     * Returns the number of the return temp. Returns -1 if not a return temp.
     */
    private static int getReturnTempNumber(IRTemp temp) {
        String name = temp.name();
        if (name.length() < 5) {
            return -1;
        }
        if (name.substring(0, 4).equals(Configuration.ABSTRACT_RET_PREFIX)) {
            return Integer.parseInt(name.substring(4));
        }
        return -1;
    }

    @Override
    public String toString() {
        String s;
        if (isArgument) {
            s = "%arg";
        } else if (isReturn) {
            s = "%ret";
        } else {
            s = "%r";
        }
        return s + id;
    }
}
