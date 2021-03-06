//package com.bwz6jk2227esl89ahj34.dataflow_analysis.tests;
//
//import com.bwz6jk2227esl89ahj34.analysis.LatticeBottom;
//import com.bwz6jk2227esl89ahj34.analysis.LatticeElement;
//import com.bwz6jk2227esl89ahj34.analysis.LatticeTop;
//import com.bwz6jk2227esl89ahj34.analysis.available_copies.AvailableCopies;
//import com.bwz6jk2227esl89ahj34.analysis.available_copies
//        .AvailableCopiesSet;
//import com.bwz6jk2227esl89ahj34.ir.IRTemp;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * Created by jihunkim on 4/21/16.
// */
//public class AvailableCopiesSetTest {
//    AvailableCopiesSet set;
//
//    // runs before every test invocation
//    @Before
//    public void setUp() {
//        Map<String, String> map = new LinkedHashMap<>();
//        map.put("x", "y");
//        map.put("a", "b");
//        set = new AvailableCopiesSet(map);
//    }
//
//    @Test
//    public void copyTest() {
//        LatticeElement copy = set.copy();
//        AvailableCopiesSet castedCopy = (AvailableCopiesSet) copy;
//
//        String temp = "x";
//        String q = set.getMap().get(temp);
//        String qq = castedCopy.getMap().get(temp);
//        Assert.assertEquals(castedCopy.getMap().get(temp), set.getMap().get(temp));
//
//        // should not be equal anymore if deep copied
//        castedCopy.getMap().put(temp, temp);
//        Assert.assertNotEquals(castedCopy.getMap().get(temp), set.getMap().get(temp));
//
//        castedCopy.intersect(new LatticeBottom());
//        // if we empty out the old one, make sure the original set is not
//        // emptied out
//        Assert.assertNotEquals(set.getMap().keySet().size(), 0);
//    }
//
//    @Test
//    public void intersectTest() {
//        // intersect with TOP
//        LatticeElement copy = set.copy();
//        AvailableCopiesSet castedCopy = (AvailableCopiesSet) copy;
//
//        castedCopy.intersect(new LatticeTop());
//
//        Assert.assertTrue(castedCopy.equals(set));
//
//        // intersect with BOTTOM
//        copy = set.copy();
//        castedCopy = (AvailableCopiesSet) copy;
//
//        castedCopy.intersect(new LatticeBottom());
//
//        Assert.assertTrue(castedCopy.equals(new AvailableCopiesSet(new LinkedHashMap<>())));
//
//        copy = set.copy();
//        castedCopy = (AvailableCopiesSet) copy;
//
//        Map<String, String> dummyMap = new LinkedHashMap<>();
//        dummyMap.put("x", "y");
//
//        castedCopy.intersect(new AvailableCopiesSet(dummyMap));
//        Assert.assertFalse(castedCopy.getMap().containsKey("a"));
//        Assert.assertTrue(castedCopy.getMap().containsKey("x"));
//        Assert.assertTrue(castedCopy.getMap().get("x").equals("y"));
//    }
//
//    @Test
//    public void unionTest() {
//        Map<String, String> dummyMap = new LinkedHashMap<>();
//        dummyMap.put("c", "d");
//
//        AvailableCopiesSet castedCopy = (AvailableCopiesSet) set.copy();
//        AvailableCopiesSet union = castedCopy.union(new AvailableCopiesSet(dummyMap));
//
//        Assert.assertTrue(union.getMap().containsKey("c"));
//        Assert.assertTrue(union.getMap().get("c").equals("d"));
//        Assert.assertTrue(union.getMap().containsKey("a"));
//        Assert.assertTrue(union.getMap().containsKey("x"));
//
//        Assert.assertEquals(union.getMap().keySet().size(), 3);
//        Assert.assertEquals(castedCopy.getMap().keySet().size(), 2);
//        Assert.assertFalse(castedCopy.getMap().containsKey("c"));
//
//        dummyMap = new LinkedHashMap<>();
//
//        // sanity check to see resetting dummy map does not screw up anything
//        Assert.assertTrue(union.getMap().containsKey("c"));
//        Assert.assertTrue(union.getMap().get("c").equals("d"));
//        Assert.assertTrue(union.getMap().containsKey("a"));
//        Assert.assertTrue(union.getMap().containsKey("x"));
//
//        Assert.assertEquals(union.getMap().keySet().size(), 3);
//        Assert.assertEquals(castedCopy.getMap().keySet().size(), 2);
//        Assert.assertFalse(castedCopy.getMap().containsKey("c"));
//
//        dummyMap = castedCopy.getMap();
//        union = castedCopy.union(new AvailableCopiesSet(dummyMap));
//        Assert.assertEquals(union.getMap().keySet().size(), 2);
//        Assert.assertFalse(union.getMap().containsKey("c"));
//
//        // with empty map
//        // this also tests equals method in conjunction
//        union = castedCopy.union(new AvailableCopiesSet(new LinkedHashMap<>()));
//        Assert.assertTrue(union.equals(castedCopy));
//        Assert.assertEquals(union.getMap().keySet().size(), 2);
//        Assert.assertFalse(union.getMap().containsKey("c"));
//    }
//
//    @Test
//    public void subtractTest() {
//        Map<String, String> dummyMap = new LinkedHashMap<>();
//        dummyMap.put("c", "d");
//
//        AvailableCopiesSet castedCopy = (AvailableCopiesSet) set.copy();
//        AvailableCopiesSet difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));
//
//        Assert.assertTrue(set.equals(difference));
//
//        castedCopy = (AvailableCopiesSet) set.copy();
//        difference = castedCopy.subtract((AvailableCopiesSet)(set.copy())) ;
//
//        Assert.assertTrue(difference.equals(new AvailableCopiesSet(new LinkedHashMap<>())));
//
//        dummyMap = new LinkedHashMap<>();
//        dummyMap.put("a", "b");
//        difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));
//
//        Assert.assertTrue(difference.getMap().containsKey("x"));
//        Assert.assertFalse(difference.getMap().containsKey("a"));
//
//        castedCopy = (AvailableCopiesSet) set.copy();
//        dummyMap = new LinkedHashMap<>();
//        dummyMap.put("a", "jihun");
//        difference = castedCopy.subtract(new AvailableCopiesSet(dummyMap));
//
//        Assert.assertTrue(difference.getMap().containsKey("a"));
//    }
//
//}
