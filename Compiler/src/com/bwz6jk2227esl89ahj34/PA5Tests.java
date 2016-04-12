package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.ir.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jihunkim on 4/11/16.
 */
public class PA5Tests {
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
        System.out.println("nice");
    }

    @Test
    public void testSomething() {
        Assert.assertTrue(true);
    }

    @Test
    public void expressionConst1() {
        Assert.assertNotEquals(assemblyInstructions, null);
        AssemblyExpression result =
                tileContainer.matchExpression(new IRConst(1), assemblyInstructions);
        Assert.assertEquals(assemblyInstructions.size(), 0); // nothing should have been added
        Assert.assertEquals(result, new AssemblyImmediate(1));
    }
}

