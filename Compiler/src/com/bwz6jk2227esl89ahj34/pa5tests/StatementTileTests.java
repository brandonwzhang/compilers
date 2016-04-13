package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.code_generation.tiles.TileContainer;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.util.Util;
import org.junit.*;
import org.junit.rules.TestName;

import java.util.LinkedList;
import java.util.List;

public class StatementTileTests {
    private List<AssemblyLine> assemblyInstructions;
    @Rule public TestName name = new TestName();
    private AssemblyInstruction move3456instruction =
            new AssemblyInstruction(
                    OpCode.ADDQ,
                    new AssemblyMemoryLocation(
                            new AssemblyAbstractRegister(
                                    new IRTemp("t0")
                            ),
                            null,
                            4
                    ),
                    new AssemblyAbstractRegister(
                            new IRTemp("t1")
                    )
    );

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
        List<AssemblyLine> result =
                TileContainer.matchStatement(new IRMove(dst, src));

        AssemblyAbstractRegister aar_dst = new AssemblyAbstractRegister(dst);
        AssemblyAbstractRegister aar_src = new AssemblyAbstractRegister(src);
        Assert.assertEquals(
                new AssemblyInstruction(OpCode.MOVQ, aar_src, aar_dst),
                result.get(1)
        );

        Util.printInstructions(name, result);
    }

    @Test
    public void jump1() {
        IRName label = new IRName("l");
        IRJump jump = new IRJump(label);
        List<AssemblyLine> result =
                TileContainer.matchStatement(jump);

        Assert.assertEquals(
                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
                result.get(1)
        );
        Util.printInstructions(name, result);
    }

    @Test(expected=AssertionError.class)
    public void label1() {
        IRLabel label = new IRLabel("l");
        List<AssemblyLine> result =
                TileContainer.matchStatement(label);

        Util.printInstructions(name, result);
        AssemblyLabel duplicateLabel = new AssemblyLabel(new AssemblyName("l"));

        Assert.assertTrue(result.get(1) instanceof AssemblyLabel);
        Assert.assertEquals((AssemblyLabel) result.get(1) , duplicateLabel);
    }

    @Test
    public void cjump1() {
        IRCJump cjump = new IRCJump(
                new IRConst(0),
                "t"
        );
        List<AssemblyLine> result =
                TileContainer.matchStatement(cjump);

        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.get(1) instanceof AssemblyInstruction);
        Assert.assertTrue(result.get(2) instanceof AssemblyInstruction);

        Assert.assertEquals(((AssemblyInstruction) result.get(1)).getOpCode(), OpCode.CMPQ);
        Assert.assertEquals(
                ((AssemblyInstruction) result.get(1)).getArgs().get(0),
                new AssemblyImmediate(0));

        Assert.assertEquals(((AssemblyInstruction) result.get(2)).getOpCode(),
                OpCode.JNE);

        Util.printInstructions(name, result);
    }

    @Test
    public void move2() {
        IRTemp temp = new IRTemp("t");
        IRCall call = new IRCall(new IRName("foo_p"));
        IRMove move = new IRMove(temp, call);
        List<AssemblyLine> result =
                TileContainer.matchStatement(move);

        Util.printInstructions(name, result);
    }

//    @Test
//    public void exp1() {
//
//    }

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
        List<AssemblyLine> result =
                TileContainer.matchStatement(moveTen);

        Util.printInstructions(name, result);
    }

    @Test
    public void move3() {
        IRTemp temp0 = new IRTemp("t0");
        IRTemp temp1 = new IRTemp("t1");
        IRStmt move3 = new IRMove(
                temp1,
                new IRBinOp(
                        OpType.ADD,
                        temp1,
                        new IRMem(
                                new IRBinOp(
                                        OpType.ADD,
                                        temp0,
                                        new IRConst(4)
                                )
                        )
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move3);

        Util.printInstructions(name, result);
        Assert.assertEquals(move3456instruction, result.get(1));
    }

    @Test
    public void move3wrongop() {
        IRTemp temp0 = new IRTemp("t0");
        IRTemp temp1 = new IRTemp("t1");
        IRStmt move3 = new IRMove(
                temp1,
                new IRBinOp(
                        OpType.MUL,
                        temp1,
                        new IRMem(
                                new IRBinOp(
                                        OpType.MUL,
                                        temp0,
                                        new IRConst(4)
                                )
                        )
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move3);

        Util.printInstructions(name, result);
        Assert.assertNotEquals(2, result.size());
    }

    @Test
    public void move4() {
        IRTemp temp0 = new IRTemp("t0");
        IRTemp temp1 = new IRTemp("t1");
        IRStmt move4 = new IRMove(
                temp1,
                new IRBinOp(
                        OpType.ADD,
                        new IRMem(
                                new IRBinOp(
                                        OpType.ADD,
                                        temp0,
                                        new IRConst(4)
                                )
                        ),
                        temp1
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move4);

        Util.printInstructions(name, result);
        Assert.assertEquals(move3456instruction, result.get(1));
    }

    @Test
    public void move5() {
        IRTemp temp0 = new IRTemp("t0");
        IRTemp temp1 = new IRTemp("t1");
        IRStmt move5 = new IRMove(
                temp1,
                new IRBinOp(
                        OpType.ADD,
                        temp1,
                        new IRMem(
                                new IRBinOp(
                                        OpType.ADD,
                                        new IRConst(4),
                                        temp0
                                )
                        )
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move5);

        Util.printInstructions(name, result);
        Assert.assertEquals(move3456instruction, result.get(1));
    }

    @Test
    public void move6() {
        IRTemp temp0 = new IRTemp("t0");
        IRTemp temp1 = new IRTemp("t1");
        IRStmt move5 = new IRMove(
                temp1,
                new IRBinOp(
                        OpType.ADD,
                        new IRMem(
                                new IRBinOp(
                                        OpType.ADD,
                                        new IRConst(4),
                                        temp0
                                )
                        ),
                        temp1
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move5);

        Util.printInstructions(name, result);
        Assert.assertEquals(move3456instruction, result.get(1));
    }

    @Test
    public void move3nomatch() {
        IRStmt move = new IRMove(
                new IRTemp("t2"),
                new IRBinOp(
                        OpType.ADD,
                        new IRTemp("t1"),
                        new IRMem(
                                new IRBinOp(
                                        OpType.ADD,
                                        new IRTemp("t0"),
                                        new IRConst(4)
                                )
                        )
                )
        );
        List<AssemblyLine> result = TileContainer.matchStatement(move);

        Util.printInstructions(name, result);
        Assert.assertNotEquals(2, result.size());
    }

//    @Test
//    public void exp1() {
//        IRExpr expr = new IRConst(4120);
//        IRExp exp = new IRExp(expr);
//        List<AssemblyLine> result =
//                TileContainer.matchStatement(exp);
//
//        Assert.assertEquals(
//                new AssemblyInstruction(OpCode.JMP, new AssemblyName("l")),
//                result.get(0)
//        );
//    }
}
