package com.bwz6jk2227esl89ahj34.pa5tests;

import com.bwz6jk2227esl89ahj34.code_generation.AbstractAssemblyGenerator;
import org.junit.Assert;
import org.junit.Test;

public class AbstractAssemblyGeneratorTests {

    @Test
    public void testSomething() { Assert.assertTrue(true); }

    @Test // num arguments for _Imain_paai should be 1
    public void numArguments1() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Imain_paai"), 1);
    }

    @Test // num return values for _Imain_paai should be 1
    public void numReturnValues1() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Imain_paai"), 0);
    }

    @Test // num arguments for _IunparseInt_aii should be 1
    public void numArguments2() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_IunparseInt_aii"), 1);
    }

    @Test // num return values for _IunparseInt_aii should be 1
    public void numReturnValues2() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_IunparseInt_aii"), 1);
    }

    @Test // num arguments for _IparseInt_t2ibai should be 1
    public void numArguments3() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_IparseInt_t2ibai"), 1);
    }

    @Test // num return values for _IparseInt_t2ibai should be 2
    public void numReturnValues3() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_IparseInt_t2ibai"), 2);
    }

    @Test // num arguments for _Ieof_b should be 0
    public void numArguments4() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Ieof_b"), 0);
    }

    @Test // num return values for _Ieof_b should be 1
    public void numReturnValues4() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Ieof_b"), 1);
    }

    @Test // num arguments for _Igcd_iii should be 2
    public void numArguments5() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Igcd_iii"), 2);
    }

    @Test // num return values for _Igcd_iii should be 1
    public void numReturnValues5() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Igcd_iii"), 1);
    }

    @Test // num arguments for _Imultiple____underScores_ should be 0
    public void numArguments6() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Imultiple____underScores_p"), 0);
    }

    @Test // num return values for _Imultiple____underScores_ should be 0
    public void numReturnValues6() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Imultiple____underScores_p"), 0);
    }

    @Test // num arguments for _Ijihun_t5iiiii
    public void numArguments7() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Ijihun_t5iiiii"), 0);
    }

    @Test // num arguments for _Ijihun_t5iiiii
    public void numReturnValues7() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Ijihun_t5iiiii"), 5);
    }

    @Test // num arguments for _Ieric_pai should be 1
    public void numArguments8() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Ieric_pai"), 1);
    }

    @Test // num arguments for _Ieric_pai should be 0
    public void numReturnValues8() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Ieric_pai"), 0);
    }

    @Test // num arguments for _Ibrandon_t2aaaaaaaaaaaiaaaaaaaaaaaaaab should be 0
    public void numArgument9() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Ibrandon_t2aaaaaaaaaaaiaaaaaaaaaaaaaab"), 0);

    }

    @Test // num arguments for _Ibrandon_t2aaaaaaaaaaaiaaaaaaaaaaaaaab should be 2
    public void numReturnValues9() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Ibrandon_t2aaaaaaaaaaaiaaaaaaaaaaaaaab"), 2);

    }

    @Test // num arguments for _Iandy_t2aaaaaaaaaaaiaaaaaaaaaaaaaabiaiab should be 3
    public void numArgument10() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numArguments("_Iandy_t2aaaaaaaaaaaiaaaaaaaaaaaaaabiaiab"), 3);

    }

    @Test // num arguments for _Iandy_t2aaaaaaaaaaaiaaaaaaaaaaaaaabiaiab should be 2
    public void numReturnValues10() {
        Assert.assertEquals(
                AbstractAssemblyGenerator.numReturnValues("_Iandy_t2aaaaaaaaaaaiaaaaaaaaaaaaaabiaiab"), 2);

    }
}
