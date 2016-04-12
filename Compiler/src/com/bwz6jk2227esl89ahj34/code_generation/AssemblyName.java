package com.bwz6jk2227esl89ahj34.code_generation;

import lombok.Data;

@Data
public class AssemblyName extends AssemblyExpression {

    private String name;

    public AssemblyName(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }
}
