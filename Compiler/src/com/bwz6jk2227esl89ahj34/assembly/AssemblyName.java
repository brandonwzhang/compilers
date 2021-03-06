package com.bwz6jk2227esl89ahj34.assembly;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyName extends AssemblyExpression {

    private String name;

    public AssemblyName(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }
}
