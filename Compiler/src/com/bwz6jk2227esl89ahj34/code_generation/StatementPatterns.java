package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

public class StatementPatterns {
    /*
            Dummy nodes that represent tile patterns. Ignore values inside, such as 0, or "".
        All StatementPatterns have roots that are IRStmts.
        null represents a generic pattern, for lack of better solution. Problems? email ahj34@cornell.edu.
    */
    IRStmt move1 = new IRMove(null, null);
    IRStmt jump1 = new IRJump((IRExpr)null);
    IRStmt label1 = new IRLabel("");


}