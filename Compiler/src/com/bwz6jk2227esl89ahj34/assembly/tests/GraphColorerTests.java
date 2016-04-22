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

    AssemblyAbstractRegister a = new AssemblyAbstractRegister(); // %0
    AssemblyAbstractRegister b = new AssemblyAbstractRegister(); // %1
    AssemblyAbstractRegister c = new AssemblyAbstractRegister(); // %2
    AssemblyAbstractRegister d = new AssemblyAbstractRegister(); // %3
    AssemblyAbstractRegister e = new AssemblyAbstractRegister(); // %4
    AssemblyAbstractRegister f = new AssemblyAbstractRegister(); // %5

    List<AssemblyAbstractRegister> an;
    List<AssemblyAbstractRegister> bn;
    List<AssemblyAbstractRegister> cn;
    List<AssemblyAbstractRegister> dn;
    List<AssemblyAbstractRegister> en;
    List<AssemblyAbstractRegister> fn;

    Map<AssemblyAbstractRegister, List<AssemblyAbstractRegister>> graph;
    List<AssemblyLine> lines;

    // runs before every test invocation
    @Before
    public void setUp() {

        an = new ArrayList<>();
        bn = new ArrayList<>();
        cn = new ArrayList<>();
        dn = new ArrayList<>();
        en = new ArrayList<>();
        fn = new ArrayList<>();

        graph = new HashMap<>();
        lines = new ArrayList<>();
    }

    @Test
    public void overlappingCoalesce() {
        /*    b+d and b+e are move pairs
         *
         *     b   e         b
         *    / \ /         / \
         *   a   c    ->   a   c
         *    \ /
         *     d
         */

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX
        };

        an.add(b); an.add(d);
        bn.add(a); bn.add(c);
        cn.add(b); cn.add(d); cn.add(e);
        dn.add(a); dn.add(c);
        en.add(c);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        lines.add(new AssemblyInstruction(OpCode.MOVQ, b, d));
        lines.add(new AssemblyInstruction(OpCode.MOVQ, b, e));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    @Test
    public void fiveNodesThreeColorsOneCoalesce() {
        /*
         *
         *     b - c
         *    / \ /|
         *   a   X |
         *    \ / \|
         *     e - d
         */

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX, AssemblyPhysicalRegister.RCX
        };

        an.add(b); an.add(e);
        bn.add(a); bn.add(c); bn.add(d);
        cn.add(b); cn.add(d); cn.add(e);
        dn.add(b); dn.add(c); dn.add(e);
        en.add(a); en.add(c); en.add(d);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        lines.add(new AssemblyInstruction(OpCode.MOVQ, a, c));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    @Test
    public void actualSpill() {
        /*  Either one or two nodes become actual spill nodes.
         *
         *     b
         *    / \
         *   a   c
         *    \ / \
         *     e - d
         */

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX
        };

        an.add(b); an.add(e);
        bn.add(a); bn.add(c);
        cn.add(b); cn.add(d); cn.add(e);
        dn.add(c); dn.add(e);
        en.add(a); en.add(c); en.add(d);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        lines.add(new AssemblyInstruction(OpCode.MOVQ, a, c));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertFalse(colored);

        printColoring(gc.getColoring());
    }

    @Test
    public void moveCoalesceThreeNode() {
        /*
         *     b
         *    / \       Nodes a and c can be coalesced.
         *   a   c
         */

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX
        };

        an.add(b);
        bn.add(a); bn.add(c);
        cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

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

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX
        };

        an.add(b); an.add(d);
        bn.add(a); bn.add(c);
        cn.add(b); cn.add(d);
        dn.add(a); dn.add(c);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn);

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

        GraphColorer.colors =
                new AssemblyPhysicalRegister[]{AssemblyPhysicalRegister.RAX, AssemblyPhysicalRegister.RBX};

        an.add(b);
        bn.add(a); bn.add(c);
        cn.add(b);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn);

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    @Test
    public void fiveNodeTest() {
        // a 5-node perfect graph with 5 available colors

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX,
                AssemblyPhysicalRegister.RBX,
                AssemblyPhysicalRegister.RCX,
                AssemblyPhysicalRegister.R8,
                AssemblyPhysicalRegister.R9
        };

        an.add(b); an.add(c); an.add(d); an.add(e);
        bn.add(a); bn.add(c); bn.add(d); bn.add(e);
        cn.add(a); cn.add(b); cn.add(d); cn.add(e);
        dn.add(a); dn.add(b); dn.add(c); dn.add(e);
        en.add(a); en.add(b); en.add(c); en.add(d);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en);

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    @Test
    public void sixNodeFreeze() {
        /*
               b   d   f
              / \ / \ /     b and e are a move pair
             a   c   e
         */

        GraphColorer.colors = new AssemblyPhysicalRegister[]{
                AssemblyPhysicalRegister.RAX,
                AssemblyPhysicalRegister.RBX
        };

        an.add(b);
        bn.add(a); bn.add(c);
        cn.add(b); cn.add(d);
        dn.add(c); dn.add(e);
        en.add(d); en.add(f);
        fn.add(e);
        graph.put(a, an); graph.put(b, bn); graph.put(c, cn); graph.put(d, dn); graph.put(e, en); graph.put(f, fn);

        lines.add(new AssemblyInstruction(OpCode.MOVQ, b, e));

        GraphColorer gc = new GraphColorer(graph, lines);
        boolean colored = gc.colorGraph();
        Assert.assertTrue(colored);

        printColoring(gc.getColoring());
    }

    public void printColoring(Map<AssemblyAbstractRegister, AssemblyPhysicalRegister> coloring) {
        System.out.println("\n" + GraphColorer.colors.length + " available colors");
        System.out.println(graph.size() + " nodes in the graph");
        System.out.println(coloring.size() + " nodes colored");
        for (AssemblyAbstractRegister n : coloring.keySet()) {
            System.out.println(n + " -> " + coloring.get(n));
        }
    }
}
