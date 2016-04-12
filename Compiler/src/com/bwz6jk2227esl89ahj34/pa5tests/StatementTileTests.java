package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.ir.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class StatementTileTests {
    private TileContainer tileContainer;
    private List<AssemblyInstruction> assemblyInstructions;

    // runs before every test invocation
    @Before
    public void setUp() {
        tileContainer = AbstractAssemblyGenerator.tileContainer;
        assemblyInstructions = new LinkedList<>();
    }

    // runs after every test invocation
    @After
    public void tearDown() {
        System.out.println("nice\n");
    }

    @Test
    public void move1() {
        IRTemp dst = new IRTemp("t0");
        IRTemp src = new IRTemp("t1");
        List<AssemblyInstruction> result =
                tileContainer.matchStatement(new IRMove(dst, src));

        AssemblyAbstractRegister aar_dst = new AssemblyAbstractRegister(dst);
        AssemblyAbstractRegister aar_src = new AssemblyAbstractRegister(src);
        Assert.assertEquals(
                new AssemblyInstruction(OpCode.MOVQ, aar_src, aar_dst),
                result.get(0)
        );
        println("TEST: move1");
        println(result.get(0).toString());
    }

    @Test
    public void jump1() {
        IRName label = new IRName("l");
        IRJump jump = new IRJump(label);
        List<AssemblyInstruction> result =
                tileContainer.matchStatement(jump);

        Assert.assertEquals(
                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
                result.get(0)
        );
        println("TEST: jump1");
        println(result.get(0).toString());
    }

    @Test(expected=AssertionError.class)
    public void label1() {
        IRLabel label = new IRLabel("l");
        List<AssemblyInstruction> result =
                tileContainer.matchStatement(label);

        println("TEST: label1");
        println(result.get(0).toString());
        AssemblyLabel duplicateLabel = new AssemblyLabel(new AssemblyName("l"));
    }

    @Test
    public void cjump1() {
        IRCJump cjump = new IRCJump(
                new IRConst(0),
                "t",
                null
        );
        List<AssemblyInstruction> result =
                tileContainer.matchStatement(cjump);

        println("TEST: cjump1");
        for (int i = 0; i < result.size(); i++) {
            println(result.get(i).toString());
        }
    }

    @Test
    public void test1() {
        IRStmt moveTen = new IRMove(
                new IRTemp("temp0"),
                new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRConst(4),
                        new IRConst(6)
                )
        );
        List<AssemblyInstruction> result =
                tileContainer.matchStatement(moveTen);

        println("TEST: test1");
        for (int i = 0; i < result.size(); i++) {
            println(result.get(i).toString());
        }
    }

    private void println(String str) {
        System.out.println(str);
    }

//    @Test
//    public void exp1() {
//        IRExpr expr = new IRConst(4120);
//        IRExp exp = new IRExp(expr);
//        List<AssemblyInstruction> result =
//                tileContainer.matchStatement(exp);
//
//        Assert.assertEquals(
//                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
//                result.get(0)
//        );
//    }
}
