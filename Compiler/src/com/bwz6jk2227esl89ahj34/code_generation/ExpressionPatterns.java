package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.*;

public class ExpressionPatterns {
    /*
            Dummy nodes that represent tile patterns. Ignore values inside, such as 0.
        null represents a generic pattern, for lack of better solution. Problems? email ahj34@cornell.edu.
    */
    IRNode const1 = new IRConst(0);
    IRNode temp1 = new IRTemp("");
    IRNode mem1 = new IRMem(null);

}