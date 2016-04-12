package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.util.Util;
import org.junit.*;
import org.junit.rules.TestName;

import java.util.LinkedList;
import java.util.List;

public class StatementTileTests {
    private List<AssemblyInstruction> assemblyInstructions;
    @Rule public TestName name = new TestName();

    // runs before every test invocation
    @Before
    public void setUp() {
        assemblyInstructions = new LinkedList<>();
    }

    // runs after every test invocation
    @After
    public void tearDown() {
        System.out.println("\nEnd of " + name.getMethodName() + "\n");
    }

    @Test
    public void move1() {
        IRTemp dst = new IRTemp("t0");
        IRTemp src = new IRTemp("t1");
        List<AssemblyInstruction> result =
                TileContainer.matchStatement(new IRMove(dst, src));

        AssemblyAbstractRegister aar_dst = new AssemblyAbstractRegister(dst);
        AssemblyAbstractRegister aar_src = new AssemblyAbstractRegister(src);
        Assert.assertEquals(
                new AssemblyInstruction(OpCode.MOVQ, aar_src, aar_dst),
                result.get(0)
        );

        Util.printInstructions(name, result);
    }

    @Test
    public void jump1() {
        IRName label = new IRName("l");
        IRJump jump = new IRJump(label);
        List<AssemblyInstruction> result =
                TileContainer.matchStatement(jump);

        Assert.assertEquals(
                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
                result.get(0)
        );
        Util.printInstructions(name, result);
    }

    @Test(expected=AssertionError.class)
    public void label1() {
        IRLabel label = new IRLabel("l");
        List<AssemblyInstruction> result =
                TileContainer.matchStatement(label);

        Util.printInstructions(name, result);
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
                TileContainer.matchStatement(cjump);

        Util.printInstructions(name, result);
    }

    @Test
    public void move2() {
        IRTemp temp = new IRTemp("t");
        IRCall call = new IRCall(new IRName("foo_p"));
        IRMove move = new IRMove(temp, call);
        List<AssemblyInstruction> result =
                TileContainer.matchStatement(move);

        Util.printInstructions(name, result);
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
                TileContainer.matchStatement(moveTen);

        Util.printInstructions(name, result);
    }


//    @Test
//    public void exp1() {
//        IRExpr expr = new IRConst(4120);
//        IRExp exp = new IRExp(expr);
//        List<AssemblyInstruction> result =
//                TileContainer.matchStatement(exp);
//
//        Assert.assertEquals(
//                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
//                result.get(0)
//        );
//    }
}
