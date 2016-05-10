package com.bwz6jk2227esl89ahj34.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Triple<A,B,C> {
    A a;
    B b;
    C c;
}
