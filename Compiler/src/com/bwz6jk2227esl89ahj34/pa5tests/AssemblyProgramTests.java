package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyProgram;
import org.junit.Assert;
import org.junit.Test;

public class AssemblyProgramTests {

    @Test
    public void testSomething() { Assert.assertTrue(true); }

    @Test // num arguments for _Imain_paai should be 1
    public void numArguments1() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_Imain_paai"), 1);
    }

    @Test // num return values for _Imain_paai should be 1
    public void numReturnValues1() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_Imain_paai"), 0);
    }

    @Test // num arguments for _IunparseInt_aii should be 1
    public void numArguments2() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_IunparseInt_aii"), 1);
    }

    @Test // num return values for _IunparseInt_aii should be 1
    public void numReturnValues2() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_IunparseInt_aii"), 1);
    }

    @Test // num arguments for _IparseInt_t2ibai should be 1
    public void numArguments3() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_IparseInt_t2ibai"), 1);
    }

    @Test // num return values for _IparseInt_t2ibai should be 2
    public void numReturnValues3() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_IparseInt_t2ibai"), 2);
    }

    @Test // num arguments for _Ieof_b should be 0
    public void numArguments4() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_Ieof_b"), 0);
    }

    @Test // num return values for _Ieof_b should be 1
    public void numReturnValues4() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_Ieof_b"), 1);
    }

    @Test // num arguments for _Igcd_iii should be 2
    public void numArguments5() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_Igcd_iii"), 2);
    }

    @Test // num return values for _Igcd_iii should be 1
    public void numReturnValues5() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_Igcd_iii"), 1);
    }

    @Test // num arguments for _Imultiple____underScores_ should be 0
    public void numArguments6() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_Imultiple____underScores_p"), 0);
    }

    @Test // num return values for _Imultiple____underScores_ should be 0
    public void numReturnValues6() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_Imultiple____underScores_p"), 0);
    }

    @Test // num arguments for _Ijihun_t5iiiii
    public void numArguments7() {
        Assert.assertEquals(
                AssemblyProgram.numArguments("_Ijihun_t5iiiii"), 0);
    }

    @Test // num arguments for _Ijihun_t5iiiii
    public void numReturnValues7() {
        Assert.assertEquals(
                AssemblyProgram.numReturnValues("_Ijihun_t5iiiii"), 5);
    }
}
