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

    AssemblyAbstractRegister a;
    AssemblyAbstractRegister b;
    AssemblyAbstractRegister c;
    AssemblyAbstractRegister d;
    AssemblyAbstractRegister e;

    List<AssemblyAbstractRegister> an;
    List<AssemblyAbstractRegister> bn;
    List<AssemblyAbstractRegister> cn;
    List<AssemblyAbstractRegister> dn;
    List<AssemblyAbstractRegister> en;

    // runs before every test invocation
    @Before
    public void setUp() {
        a = new AssemblyAbstractRegister();
        b = new AssemblyAbstractRegister();
        c = new AssemblyAbstractRegister();
        d = new AssemblyAbstractRegister();
        e = new AssemblyAbstractRegister();

        an = new ArrayList<>();
        bn = new ArrayList<>();
        cn = new ArrayList<>();
        dn = new ArrayList<>();
        en = new ArrayList<>();
    }

    @Test
    public void moveCoalesceThreeNode() {
        /*
         *     b
         *    / \       Nodes a and c can be coalesced.
         *   a   c
         */

        AssemblyPhysicalRegister[] colors = {AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX};
        GraphColorer.colors = colors;

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        an.add(b);
        bn.add(a); bn.add(c);
        cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        List<AssemblyLine> lines = new ArrayList<>();
        lines.add(new AssemblyInstruction(OpCode.MOVQ, a, c));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);
        Assert.assertEquals(0, lines.size());

        printColoring(gc.getColoring());
        System.out.println(lines.size());
    }

    @Test
    public void moveCoalesceFourNode() {
        /*
         *     b
         *    / \
         *   a   c      a+c and b+d are candidate pairs for coalescing
         *    \ /
         *     d
         */

        AssemblyPhysicalRegister[] colors = {AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX};
        GraphColorer.colors = colors;

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        an.add(b); an.add(d);
        bn.add(a); bn.add(c);
        cn.add(b); cn.add(d);
        dn.add(a); dn.add(c);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn);

        List<AssemblyLine> lines = new ArrayList<>();
        lines.add(new AssemblyInstruction(OpCode.MOVQ, a, c));
        lines.add(new AssemblyInstruction(OpCode.MOVQ, b, d));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
        System.out.println(lines);
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

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        an.add(b);
        bn.add(a); bn.add(c);
        cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        GraphColorer gc = new GraphColorer(graph, new ArrayList<>());
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
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

        Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph = new HashMap<>();
        an.add(b); an.add(c); an.add(d); an.add(e);
        bn.add(a); bn.add(c); bn.add(d); bn.add(e);
        cn.add(a); cn.add(b); cn.add(d); cn.add(e);
        dn.add(a); dn.add(b); dn.add(c); dn.add(e);
        en.add(a); en.add(b); en.add(c); en.add(d);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        GraphColorer gc = new GraphColorer(graph, new ArrayList<>());
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    public static void printColoring(Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring) {
        for (AssemblyAbstractRegister n : coloring.keySet()) {
            System.out.println(coloring.get(n));
        }
    }
}
