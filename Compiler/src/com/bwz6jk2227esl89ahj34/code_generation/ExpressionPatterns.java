package com.bwz6jk2227esl89ahj34.code_generation;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
public class ExpressionPatterns {
    /*
            Dummy nodes that represent tile patterns. Ignore values inside, such as 0.
        null represents a generic pattern, for lack of better solution. Problems? email ahj34@cornell.edu.
    */
    public static IRExpr const1 = new IRConst(0);
    public static IRExpr temp1 = new IRTemp("");
    public static IRExpr mem1 = new IRMem((IRExpr)(null));
    public static IRExpr name1 = new IRName("");

    public static IRExpr binop1 = new IRBinOp(null, (IRExpr)(null), (IRExpr)(null));


    public static IRExpr mem2 =
            new IRMem(new IRBinOp(OpType.ADD, new IRConst(0), new IRTemp("")));
    public static IRExpr mem3 =
            new IRMem(new IRBinOp(OpType.ADD, new IRTemp(""), new IRConst(0)));
    public static IRExpr mem4 =
            new IRMem(new IRBinOp(OpType.SUB, new IRConst(0), new IRTemp("")));
    public static IRExpr mem5 =
            new IRMem(new IRBinOp(OpType.SUB, new IRTemp(""), new IRConst(0)));
    public static IRExpr mem6 =
            new IRMem(new IRBinOp(OpType.ADD, new IRTemp(""), new IRTemp("")));

}