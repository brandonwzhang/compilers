package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

public class StatementPatterns {
    /*
            Dummy nodes that represent tile patterns. Ignore values inside, such as 0, or "".
        All StatementPatterns have roots that are IRStmts.
        null represents a generic pattern, for lack of better solution. Problems? email ahj34@cornell.edu.
    */
    public static IRStmt move1 = new IRMove(null, null);
    public static IRStmt jump1 = new IRJump((IRExpr)null); // fuck Java
    public static IRStmt label1 = new IRLabel("");
    public static IRStmt exp1 = new IRExp(new IRCall(new IRName("")));
    public static IRStmt move2 = new IRMove(new IRTemp(""), new IRCall(new IRName("")));

    public static IRStmt return1 = new IRReturn();

    public static IRStmt cjump1 = new IRCJump(null, "");

    /*
       Some non-trivial tiles
    */

    public static IRStmt move3 = new IRMove(new IRTemp(""),
            new IRBinOp(
                    OpType.ADD,
                    new IRTemp(""),
                    new IRMem(
                            new IRBinOp(
                                    OpType.ADD,
                                    new IRTemp(""),
                                    new IRConst(0)
                            )
                    )
            )
    );

    public static IRStmt move4 = new IRMove(new IRTemp(""),
            new IRBinOp(
                    OpType.ADD,
                    new IRMem(
                            new IRBinOp(
                                    OpType.ADD,
                                    new IRTemp(""),
                                    new IRConst(0)
                            )
                    ),
                    new IRTemp("")
            )
    );

    public static IRStmt move5 = new IRMove(new IRTemp(""),
            new IRBinOp(
                    OpType.ADD,
                    new IRTemp(""),
                    new IRMem(
                            new IRBinOp(
                                    OpType.ADD,
                                    new IRTemp(""),
                                    new IRConst(0)
                            )
                    )
            )
    );

    public static IRStmt move6 = new IRMove(new IRTemp(""),
            new IRBinOp(
                    OpType.ADD,
                    new IRTemp(""),
                    new IRMem(
                            new IRBinOp(
                                    OpType.ADD,
                                    new IRConst(0),
                                    new IRTemp("")
                            )
                    )
            )
    );

}