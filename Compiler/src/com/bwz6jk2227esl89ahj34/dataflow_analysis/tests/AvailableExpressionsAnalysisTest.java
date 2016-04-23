package com.bwz6jk2227esl89ahj34.dataflow_analysis.tests;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.DataflowAnalysis.*;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions.AvailableExpressionsAnalysis;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AvailableExpressionsAnalysisTest {
    AvailableExpressionsAnalysis analysis;

    @Before
    public void setup() {

    }


    @Test
    public void evalTest() {
    }

    @Test
    public void exprEqualsTest() {
        IRExpr e1 = new IRConst(1);
        IRExpr e1_ = new IRConst(1);
        Assert.assertTrue(analysis.exprEquals(e1, e1_));

        IRExpr e2 = new IRConst(2);
        IRExpr e2_ = new IRConst(3);
        Assert.assertFalse(analysis.exprEquals(e2, e2_));

        IRExpr e3 = new IRTemp("lag");
        IRExpr e3_ = new IRTemp("lag");
        Assert.assertTrue(analysis.exprEquals(e3, e3_));
        IRExpr e3__ = new IRTemp("hello?");
        Assert.assertFalse(analysis.exprEquals(e3, e3__));

        IRExpr e4 = new IRBinOp(OpType.ADD, e1, e1_);
        IRExpr e4_ = new IRBinOp(OpType.ADD, e1, e1_);
        Assert.assertTrue(analysis.exprEquals(e4, e4_));
        IRExpr e4__ = new IRBinOp(OpType.MUL, e1, e1_);
        Assert.assertFalse(analysis.exprEquals(e4, e4__));
        IRExpr e4___ = new IRBinOp(OpType.ADD, e1, e2_);
        Assert.assertFalse(analysis.exprEquals(e4,e4___));


        IRExpr e5 = new IRMem(e4);
        IRExpr e5_ = new IRMem(e4_);
        Assert.assertTrue(analysis.exprEquals(e5, e5_));

        // TODO tests for call and failing tests for mem

    }
}
