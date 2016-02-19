package com.AST;

import lombok.Data;

public @Data class Pair<A, B> {
    A car;
    B cdr;

    public Pair(A car, B cdr) {
        this.car = car;
        this.cdr = cdr;
    }
}
