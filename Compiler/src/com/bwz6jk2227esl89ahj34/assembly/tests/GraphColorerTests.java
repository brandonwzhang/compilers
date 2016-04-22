package com.bwz6jk2227esl89ahj34.assembly.tests;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyAbstractRegister;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyPhysicalRegister;
import com.bwz6jk2227esl89ahj34.assembly.register_allocation.GraphColorer;
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
    public void moveCoalesceThreeNode() {
        /*
         *     b
         *    / \
         *   a   c
         */

        AssemblyPhysicalRegister[] colors = {AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX};
        GraphColorer.colors = colors;

        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
        AssemblyAbstractRegister c = new AssemblyAbstractRegister();

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        List<AssemblyAbstractRegister> an = new ArrayList<>(); an.add(b);
        List<AssemblyAbstractRegister> bn = new ArrayList<>(); bn.add(a); bn.add(c);
        List<AssemblyAbstractRegister> cn = new ArrayList<>(); cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        List<AssemblyLine> lines = new ArrayList<>();
        lines.add(new AssemblyInstruction(OpCode.MOVQ, a, c));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);
        Assert.assertEquals(0, lines.size());

        Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring = gc.getColoring();
        System.out.println(coloring.get(a));
        System.out.println(coloring.get(b));
        System.out.println(coloring.get(c));
        System.out.println(lines.size());
    }

    @Test
    public void threeNodeTest() {
        /*
         *     b
         *    / \
         *   a   c
         */

        AssemblyPhysicalRegister[] colors = {AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX};
        GraphColorer.colors = colors;

        AssemblyAbstractRegister a = new AssemblyAbstractRegister();
        AssemblyAbstractRegister b = new AssemblyAbstractRegister();
        AssemblyAbstractRegister c = new AssemblyAbstractRegister();

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        List<AssemblyAbstractRegister> an = new ArrayList<>(); an.add(b);
        List<AssemblyAbstractRegister> bn = new ArrayList<>(); bn.add(a); bn.add(c);
        List<AssemblyAbstractRegister> cn = new ArrayList<>(); cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        GraphColorer gc = new GraphColorer(graph, new ArrayList<>());
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring = gc.getColoring();
        System.out.println(coloring.get(a));
        System.out.println(coloring.get(b));
        System.out.println(coloring.get(c));
    }

    @Test
    public void fiveNodeTest() {
        // a 5-node perfect graph with 5 available colors

        AssemblyPhysicalRegister[] colors = {
                AssemblyPhysicalRegister.RAX,
                AssemblyPhysicalRegister.RBX,
                AssemblyPhysicalRegister.RCX,
                AssemblyPhysicalRegister.R8,
                AssemblyPhysicalRegister.R9
        };
        GraphColorer.colors = colors;

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

        GraphColorer gc = new GraphColorer(graph, new ArrayList<>());
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring = gc.getColoring();
        System.out.println(coloring.get(a));
        System.out.println(coloring.get(b));
        System.out.println(coloring.get(c));
        System.out.println(coloring.get(d));
        System.out.println(coloring.get(e));
    }
}
