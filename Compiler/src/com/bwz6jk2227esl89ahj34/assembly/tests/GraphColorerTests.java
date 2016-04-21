package com.bwz6jk2227esl89ahj34.assembly.tests;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister.Register;
import com.bwz6jk2227esl89ahj34.assembly.register_allocation.GraphColorer2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphColorerTests {

    // runs before every test invocation
    @Before
    public void setUp() {

    }

    @Test
    public void threeNodeTest() {
        /*
         *     b
         *    / \
         *   a   c
         */

        Register[] colors = {Register.RAX, Register.RBX};
        GraphColorer2.colors = colors;

        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
        AssemblyAbstractRegister c = new AssemblyAbstractRegister();

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        List<AssemblyAbstractRegister> an = new ArrayList<>(); an.add(b);
        List<AssemblyAbstractRegister> bn = new ArrayList<>(); bn.add(a); bn.add(c);
        List<AssemblyAbstractRegister> cn = new ArrayList<>(); cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        GraphColorer2 gc = new GraphColorer2(graph);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        Map<AssemblyAbstractRegister, Register> coloring = gc.getColoring();
        System.out.println(coloring.get(a));
        System.out.println(coloring.get(b));
        System.out.println(coloring.get(c));
    }

    @Test
    public void fiveNodeTest() {
        // a 5-node perfect graph with 5 available colors

        Register[] colors = {Register.RAX, Register.RBX, Register.RCX, Register.R8, Register.R9};
        GraphColorer2.colors = colors;

        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
        AssemblyAbstractRegister c = new AssemblyAbstractRegister();
        AssemblyAbstractRegister d = new AssemblyAbstractRegister();
        AssemblyAbstractRegister e = new AssemblyAbstractRegister();

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        List<AssemblyAbstractRegister> an = new ArrayList<>(); an.add(b); an.add(c); an.add(d); an.add(e);
        List<AssemblyAbstractRegister> bn = new ArrayList<>(); bn.add(a); bn.add(c); bn.add(d); bn.add(e);
        List<AssemblyAbstractRegister> cn = new ArrayList<>(); cn.add(a); cn.add(b); cn.add(d); cn.add(e);
        List<AssemblyAbstractRegister> dn = new ArrayList<>(); dn.add(a); dn.add(b); dn.add(c); dn.add(e);
        List<AssemblyAbstractRegister> en = new ArrayList<>(); en.add(a); en.add(b); en.add(c); en.add(d);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        GraphColorer2 gc = new GraphColorer2(graph);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        Map<AssemblyAbstractRegister, Register> coloring = gc.getColoring();
        System.out.println(coloring.get(a));
        System.out.println(coloring.get(b));
        System.out.println(coloring.get(c));
        System.out.println(coloring.get(d));
        System.out.println(coloring.get(e));
    }
}
