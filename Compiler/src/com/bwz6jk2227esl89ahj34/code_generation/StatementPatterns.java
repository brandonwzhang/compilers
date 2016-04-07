package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

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

}