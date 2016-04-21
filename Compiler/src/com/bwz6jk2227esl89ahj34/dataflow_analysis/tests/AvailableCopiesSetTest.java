package com.bwz6jk2227esl89ahj34.dataflow_analysis.tests;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
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
        IRTemp q = set.getMap().get(temp);
        IRTemp qq = castedCopy.getMap().get(temp);
        Assert.assertEquals(castedCopy.getMap().get(temp), set.getMap().get(temp));

        // should not be equal anymore if deep copied
        castedCopy.getMap().put(temp, temp);
        Assert.assertNotEquals(castedCopy.getMap().get(temp), set.getMap().get(temp));

        castedCopy.intersect(new LatticeBottom());
        // if we empty out the old one, make sure the original set is not
        // emptied out
        Assert.assertNotEquals(set.getMap().keySet().size(), 0);
    }

    @Test
    public void intersectTest() {
        // intersect with TOP
        LatticeElement copy = set.copy();
        AvailableCopiesSet castedCopy = (AvailableCopiesSet) copy;

        castedCopy.intersect(new LatticeTop());

        Assert.assertTrue(castedCopy.equals(set));

        // intersect with BOTTOM
        copy = set.copy();
        castedCopy = (AvailableCopiesSet) copy;

        castedCopy.intersect(new LatticeBottom());

        Assert.assertTrue(castedCopy.equals(new AvailableCopiesSet(new HashMap<>())));

        copy = set.copy();
        castedCopy = (AvailableCopiesSet) copy;

        Map<IRTemp, IRTemp> dummyMap = new HashMap<>();
        dummyMap.put(new IRTemp("x"), new IRTemp("y"));

        castedCopy.intersect(new AvailableCopiesSet(dummyMap));
        Assert.assertFalse(castedCopy.getMap().containsKey(new IRTemp("a")));
        Assert.assertTrue(castedCopy.getMap().containsKey(new IRTemp("x")));
        Assert.assertTrue(castedCopy.getMap().get(new IRTemp("x")).equals(new IRTemp("y")));
    }


}
