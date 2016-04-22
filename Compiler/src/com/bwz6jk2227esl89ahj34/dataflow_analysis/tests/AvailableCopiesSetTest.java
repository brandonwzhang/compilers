package com.bwz6jk2227esl89ahj34.dataflow_analysis.tests;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeBottom;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeTop;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies.AvailableCopies;
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

    @Test
    public void unionTest() {
        Map<IRTemp, IRTemp> dummyMap = new HashMap<>();
        dummyMap.put(new IRTemp("c"), new IRTemp("d"));

        AvailableCopiesSet castedCopy = (AvailableCopiesSet) set.copy();
        AvailableCopiesSet union = castedCopy.union(new AvailableCopiesSet(dummyMap));

        Assert.assertTrue(union.getMap().containsKey(new IRTemp("c")));
        Assert.assertTrue(union.getMap().get(new IRTemp("c")).equals(new IRTemp("d")));
        Assert.assertTrue(union.getMap().containsKey(new IRTemp("a")));
        Assert.assertTrue(union.getMap().containsKey(new IRTemp("x")));

        Assert.assertEquals(union.getMap().keySet().size(), 3);
        Assert.assertEquals(castedCopy.getMap().keySet().size(), 2);
        Assert.assertFalse(castedCopy.getMap().containsKey(new IRTemp("c")));

        dummyMap = new HashMap<>();

        // sanity check to see resetting dummy map does not screw up anything
        Assert.assertTrue(union.getMap().containsKey(new IRTemp("c")));
        Assert.assertTrue(union.getMap().get(new IRTemp("c")).equals(new IRTemp("d")));
        Assert.assertTrue(union.getMap().containsKey(new IRTemp("a")));
        Assert.assertTrue(union.getMap().containsKey(new IRTemp("x")));

        Assert.assertEquals(union.getMap().keySet().size(), 3);
        Assert.assertEquals(castedCopy.getMap().keySet().size(), 2);
        Assert.assertFalse(castedCopy.getMap().containsKey(new IRTemp("c")));

        dummyMap = castedCopy.getMap();
        union = castedCopy.union(new AvailableCopiesSet(dummyMap));
        Assert.assertEquals(union.getMap().keySet().size(), 2);
        Assert.assertFalse(union.getMap().containsKey(new IRTemp("c")));

        // with empty map
        // this also tests equals method in conjunction
        union = castedCopy.union(new AvailableCopiesSet(new HashMap<>()));
        Assert.assertTrue(union.equals(castedCopy));
        Assert.assertEquals(union.getMap().keySet().size(), 2);
        Assert.assertFalse(union.getMap().containsKey(new IRTemp("c")));
    }

    @Test
    public void subtractTest() {
        Map<IRTemp, IRTemp> dummyMap = new HashMap<>();
        dummyMap.put(new IRTemp("c"), new IRTemp("d"));

        AvailableCopiesSet castedCopy = (AvailableCopiesSet) set.copy();
        AvailableCopiesSet difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));

        Assert.assertTrue(set.equals(difference));

        castedCopy = (AvailableCopiesSet) set.copy();
        difference = castedCopy.subtract((AvailableCopiesSet)(set.copy())) ;

        Assert.assertTrue(difference.equals(new AvailableCopiesSet(new HashMap<>())));

        dummyMap = new HashMap<>();
        dummyMap.put(new IRTemp("a"), new IRTemp("b"));
        difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));

        Assert.assertTrue(difference.getMap().containsKey(new IRTemp("x")));
        Assert.assertFalse(difference.getMap().containsKey(new IRTemp("a")));

        castedCopy = (AvailableCopiesSet) set.copy();
        dummyMap = new HashMap<>();
        dummyMap.put(new IRTemp("a"), new IRTemp("jihun"));
        difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));

        Assert.assertTrue(difference.getMap().containsKey(new IRTemp("a")));
    }

}
