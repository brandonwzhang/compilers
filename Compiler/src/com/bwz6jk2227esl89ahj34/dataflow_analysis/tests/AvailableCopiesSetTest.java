package com.bwz6jk2227esl89ahj34.dataflow_analysis.tests;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies.AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by jihunkim on 4/21/16.
 */
public class AvailableCopiesSetTest {
    AvailableCopiesSet set;

    // runs before every test invocation
    @Before
    public void setUp() {
        Map<IRTemp, IRTemp> map = new HashMap<>();
        map.put(new IRTemp("x"), new IRTemp("y"));
        map.put(new IRTemp("a"), new IRTemp("b"));
        set = new AvailableCopiesSet(map);
    }

    @Test
    public void copyTest() {
        LatticeElement copy = set.copy();
        AvailableCopiesSet castedCopy = (AvailableCopiesSet) copy;

        IRTemp temp = new IRTemp("x");
        assert castedCopy.getMap().get(temp).equals(set.getMap().get(temp));

        // should not be equal anymore if deep copied
        castedCopy.getMap().put(temp, temp);
        assert !castedCopy.getMap().get(temp).equals(set.getMap().get(temp));

        castedCopy.intersect(new LatticeBottom());
        // if we empty out the old one, make sure the original set is not
        // emptied out
        assert set.getMap().keySet().size() != 0;
    }


}
