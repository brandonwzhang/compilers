package com.bwz6jk2227esl89ahj34.ir.interpret;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.IRCall;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.IRConst;
import com.bwz6jk2227esl89ahj34.ir.IRFuncDecl;
import com.bwz6jk2227esl89ahj34.ir.IRMove;
import com.bwz6jk2227esl89ahj34.ir.IRName;
import com.bwz6jk2227esl89ahj34.ir.IRReturn;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;

public class Main {

    public static void main(String[] args) {
        // Runs a simple arithmetic expression in the simulator
        // IR roughly corresponds to the following:
        //      a(i:int, j:int):int {
        //          return i + (2 * j);
        //      }
        //      b(i:int, j:int):int {
        //          return a(i, 5 * j);
        //      }

        String r0 = Configuration.ABSTRACT_REG_PREFIX + 0;
        String r1 = Configuration.ABSTRACT_REG_PREFIX + 1;
        String RV = Configuration.RV_NAME;

        IRStmt aBody = new IRSeq(new IRMove(new IRTemp("i"), new IRTemp(r0)),
                                 new IRMove(new IRTemp("j"), new IRTemp(r1)),
                                 new IRMove(new IRTemp(RV),
                                            new IRBinOp(OpType.ADD,
                                                        new IRTemp("i"),
                                                        new IRBinOp(OpType.MUL,
                                                                    new IRConst(2),
                                                                    new IRTemp("j")))),
                                 new IRReturn());
        IRFuncDecl aFunc = new IRFuncDecl("a", aBody, false);

        IRStmt bBody = new IRSeq(new IRMove(new IRTemp("i"), new IRTemp(r0)),
                                 new IRMove(new IRTemp("j"), new IRTemp(r1)),
                                 new IRMove(new IRTemp(RV),
                                            new IRCall(new IRName("a"), 1, 
                                                       new IRTemp("i"),
                                                       new IRBinOp(OpType.MUL,
                                                    		   new IRConst(5),
                                                    		   new IRTemp("j")))),
                                                       
                                 new IRReturn());
        IRFuncDecl bFunc = new IRFuncDecl("b", bBody, false);

        IRCompUnit compUnit = new IRCompUnit("test");
        compUnit.appendFunc(aFunc);
        compUnit.appendFunc(bFunc);
        
        SExpPrinter sp = new CodeWriterSExpPrinter(System.out);
        System.out.println("Code:");
        compUnit.printSExp(sp);
        sp.flush();

        IRSimulator sim = new IRSimulator(compUnit);
        long result = sim.call("b", 2, 1);
        System.out.println();
        System.out.println("b(2,1) == " + result);
    }
}
