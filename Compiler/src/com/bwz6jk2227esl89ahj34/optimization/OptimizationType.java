package com.bwz6jk2227esl89ahj34.optimization;

public enum OptimizationType {
    CF, REG, MC, UCE, CSE, ALG, COPY, DCE,
    INL, SR, LU, LICM, PRE, CP, VN;

    public static final OptimizationType[] SUPPORTED_OPTIMIZATIONS =
            {CF, REG, MC, UCE, CSE, COPY};

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
