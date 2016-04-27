package com.bwz6jk2227esl89ahj34.optimization;

public enum Optimization {
    CF, REG, MC, UCE, CSE, ALG, COPY, DCE,
    INL, SR, LU, LICM, PRE, CP, VN;

    public static final Optimization[] SUPPORTED_OPTIMIZATIONS =
            {CF, REG, MC, UCE, CSE, COPY};

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
