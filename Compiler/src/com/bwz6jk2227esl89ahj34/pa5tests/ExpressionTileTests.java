package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ExpressionTileTests {
    private List<AssemblyInstruction> assemblyInstructions;

    // runs before every test invocation
    @Before
    public void setUp() {
        assemblyInstructions = new LinkedList<>();
    }

    // runs after every test invocation
    @After
    public void tearDown() {
        System.out.println("nice");
    }

    @Test
    public void testSomething() {
        Assert.assertTrue(true);
    }

    @Test
    public void expressionGetPatternSizeTest() {
        Tile nullTile = new ExpressionTile(null, null);
        Assert.assertEquals(nullTile.size, 0);

        Tile const1Tile = new ExpressionTile(ExpressionPatterns.const1,
                ExpressionCodeGenerators.const1);
        Assert.assertEquals(const1Tile.size, 1);

        Tile temp1Tile = new ExpressionTile(ExpressionPatterns.temp1,
                ExpressionCodeGenerators.temp1);
        Assert.assertEquals(temp1Tile.size, 1);

        Tile mem1Tile = new ExpressionTile(ExpressionPatterns.mem1,
                ExpressionCodeGenerators.mem1);
        Assert.assertEquals(mem1Tile.size, 1);

        Tile name1Tile = new ExpressionTile(ExpressionPatterns.name1,
                ExpressionCodeGenerators.name1);
        Assert.assertEquals(name1Tile.size, 1);

        Tile binop1Tile = new ExpressionTile(ExpressionPatterns.binop1,
                ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop1Tile.size, 1);

        Tile temp2Tile = new ExpressionTile(new IRTemp("test"),
                ExpressionCodeGenerators.temp1);
        Assert.assertEquals(temp2Tile.size, 1);

        Tile mem2Tile = new ExpressionTile(new IRMem(new IRTemp("x")),
                ExpressionCodeGenerators.mem1);
        Assert.assertEquals(mem2Tile.size, 2);

        Tile binop2Tile = new ExpressionTile(new IRBinOp(
                OpType.ADD,
                new IRMem(new IRTemp("x")),
                new IRConst(1)
                ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop2Tile.size, 4);

        Tile binop3Tile = new ExpressionTile(new IRBinOp(
                IRBinOp.OpType.ADD,
                new IRConst(1),
                new IRConst(1)
        ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop3Tile.size, 3);

        Tile binop4Tile = new ExpressionTile(new IRBinOp(
                OpType.ADD,
                new IRConst(1),
                new IRMem(new IRTemp("x"))
        ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop4Tile.size, 4);

        Tile binop5Tile = new ExpressionTile(new IRBinOp(
                OpType.ADD,
                new IRTemp("x"),
                new IRTemp("x")
        ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop5Tile.size, 3);

        Tile binop6Tile = new ExpressionTile(new IRBinOp(
                IRBinOp.OpType.ADD,
                new IRTemp("x"),
                new IRConst(1)
        ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop6Tile.size, 3);

        Tile binop7Tile = new ExpressionTile(new IRBinOp(
                OpType.ADD,
                new IRConst(1),
                new IRTemp("x")
        ), ExpressionCodeGenerators.binop1);
        Assert.assertEquals(binop7Tile.size, 3);
    }

    @Test // CONST(1)
    public void expressionConst1() {
        Assert.assertNotEquals(assemblyInstructions, null);
        AssemblyExpression result =
                TileContainer.matchExpression(new IRConst(1), assemblyInstructions);

        //nothing should have been added
        Assert.assertEquals(assemblyInstructions.size(), 0);
        Assert.assertEquals(result, new AssemblyImmediate(1));
    }

    @Test // TEMP(x)
    public void expressionTemp1() {
        AssemblyExpression result =
                TileContainer.matchExpression(new IRTemp("x"), assemblyInstructions);

        //nothing should have been added
        Assert.assertEquals(assemblyInstructions.size(), 0 );
        Assert.assertEquals(result, new AssemblyAbstractRegister(new IRTemp("x")));
    }

    @Test // MEM(TEMP(x))
    public void expressionMem1() {
        AssemblyExpression result =
                TileContainer.matchExpression(new IRMem(new IRTemp("x")), assemblyInstructions);

        //nothing should have been added
        Assert.assertEquals(assemblyInstructions.size(), 0 );
        Assert.assertEquals(result,
                new AssemblyMemoryLocation(
                        new AssemblyAbstractRegister(new IRTemp("x"))));
    }

    @Test // binop within mem MEM(TEMP(x) + 1) this also tests TEMP(x) + 1
    public void expresssionMem2() {
        AssemblyExpression result =
                TileContainer.matchExpression(new IRMem(new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRTemp("x"),
                        new IRConst(1)
                )), assemblyInstructions);
        Assert.assertEquals(assemblyInstructions.size() , 2);
        AssemblyInstruction moveInstruction = assemblyInstructions.get(0);
        Assert.assertEquals(moveInstruction.getOpCode(), OpCode.MOVQ);
        List<AssemblyExpression> moveInstructionArgs = moveInstruction.getArgs();
        Assert.assertEquals(moveInstructionArgs.size(), 2);
        Assert.assertTrue(moveInstructionArgs.get(0) instanceof AssemblyAbstractRegister);
        Assert.assertEquals(((AssemblyAbstractRegister) (moveInstructionArgs.get(0)) ).id, 0);
        Assert.assertTrue(moveInstructionArgs.get(1) instanceof AssemblyAbstractRegister);
        Assert.assertEquals(((AssemblyAbstractRegister) (moveInstructionArgs.get(1))).id, 1);
        Assert.assertTrue(result instanceof AssemblyMemoryLocation);
        Assert.assertTrue(((AssemblyMemoryLocation) result).getBaseRegister()
                instanceof AssemblyAbstractRegister);
        Assert.assertEquals(
                ((AssemblyAbstractRegister)((AssemblyMemoryLocation) result)
                        .getBaseRegister()).id, 1);
    }

    @Test // BINOP(+,1,1)
    public void expressionBinop1() {
        AssemblyExpression result =
                TileContainer.matchExpression(new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRConst(1),
                        new IRConst(1)
                ), assemblyInstructions);

        Assert.assertTrue(result instanceof AssemblyAbstractRegister);
        Assert.assertEquals(assemblyInstructions.size(), 2);
        Assert.assertEquals(assemblyInstructions.get(0).getOpCode(), OpCode.MOVQ);
        Assert.assertEquals(
                ((AssemblyImmediate)
                        (assemblyInstructions.get(0).getArgs().get(0))),
                new AssemblyImmediate(1)
                );
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(0).getArgs().get(1))).id,
                0
        );
        Assert.assertEquals(
                ((AssemblyImmediate)
                        (assemblyInstructions.get(1).getArgs().get(0))),
                new AssemblyImmediate(1)
        );
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(1).getArgs().get(1))).id,
                0
        );
        Assert.assertEquals(assemblyInstructions.get(1).getOpCode(), OpCode.ADDQ);
    }

    @Test // BINOP(+,x,y)
    public void expressionBinop2() {
        AssemblyExpression result =
                TileContainer.matchExpression(new IRBinOp(
                        IRBinOp.OpType.ADD,
                        new IRTemp("x"),
                        new IRTemp("y")
                ), assemblyInstructions);

        Assert.assertTrue(result instanceof AssemblyAbstractRegister);
        Assert.assertEquals(AssemblyAbstractRegister.getCurId(), 3);
        Assert.assertEquals(assemblyInstructions.size(), 2);
        Assert.assertEquals(assemblyInstructions.get(0).getOpCode(), OpCode.MOVQ);
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(0).getArgs().get(0))).id,
                0
        );
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(0).getArgs().get(1))).id,
                2
        );
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(1).getArgs().get(0))).id,
                1
        );
        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(1).getArgs().get(1))).id,
                2
        );

        Assert.assertEquals(
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(0).getArgs().get(1))).id,
                ((AssemblyAbstractRegister)
                        (assemblyInstructions.get(0).getArgs().get(1))).id
        );
        Assert.assertEquals(assemblyInstructions.get(1).getOpCode(), OpCode.ADDQ);
    }
}

