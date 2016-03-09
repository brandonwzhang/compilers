package com.bwz6jk2227esl89ahj34.ir.interpret;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp;
import com.bwz6jk2227esl89ahj34.ir.IRConst;
import com.bwz6jk2227esl89ahj34.ir.IRExpr;
import com.bwz6jk2227esl89ahj34.ir.IRMem;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;

public class Configuration {
    /* Some special stack-related names that are used in the IR */
    public static final String CALL_RV_SCRATCH = "RV_SCRATCH";
    public static final String CALL_ARG_SCRATCH = "ARG_SCRATCH";
    public static final String SPILL_SCRATCH = "SPILL_SCRATCH";
    public static final String MEM_NAME = "MEM";
    public static final String FP_NAME = "FP";
    public static final String ABSTRACT_REG_PREFIX = "REG";
    public static final String RV_NAME = "RV";
    public static final String THIS_NAME = "THIS";
    public static final String OFFSET_NAME = "OFFSET";

    /* assumes 64-bit arch */
    public static int WORD_SIZE = 8;
    /* System V calling conventions */
    public static String[] PARAMETER_REGISTERS =
            new String[] { "rdi", "rsi", "rdx", "rcx", "r8", "r9" };

    /// Return a simple alias name for certain memory expressions
    public static String alias(IRMem mem) {
        IRExpr memExpr = mem.expr();
        String tempName = null;

        if (memExpr instanceof IRTemp) {
            tempName = ((IRTemp) memExpr).name();
        }
        else if (memExpr instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) memExpr;
            IRExpr left = binOp.left();
            IRExpr right = binOp.right();

            if (!(right instanceof IRConst) || !(left instanceof IRTemp))
                return MEM_NAME;

            tempName = ((IRTemp) left).name();
        }

        if (tempName != null && (tempName.equals(CALL_RV_SCRATCH)
                || tempName.equals(CALL_ARG_SCRATCH)
                || tempName.equals(FP_NAME)))
            return tempName;

        return MEM_NAME;
    }
}
