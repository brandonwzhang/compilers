package com.bwz6jk2227esl89ahj34.code_generation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyImmediate extends AssemblyExpression{
    private long value;

    public AssemblyImmediate(long l) {
        value = l;
    }
    @Override
    public String toString() {
        return "$" + value;
    }
}
