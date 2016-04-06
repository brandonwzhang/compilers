package com.bwz6jk2227esl89ahj34.AST.code_generation;

public class AssemblyMemoryLocation extends AssemblyExpression {
    private long displacement;
    private long scale;
    private AssemblyRegister offsetRegister;
    private AssemblyRegister baseRegister;

    public AssemblyMemoryLocation(AssemblyRegister baseRegister) {
        this.baseRegister = baseRegister;
    }

    public AssemblyMemoryLocation(AssemblyRegister baseRegister,
                                  AssemblyRegister offsetRegister) {
        this.baseRegister = baseRegister;
        this.offsetRegister = offsetRegister;
    }

    public AssemblyMemoryLocation(AssemblyRegister baseRegister,
                                  AssemblyRegister offsetRegister,
                                  long displacement) {
        this.baseRegister = baseRegister;
        this.offsetRegister = offsetRegister;
        this.displacement = displacement;
    }

    public AssemblyMemoryLocation(AssemblyRegister baseRegister,
                                  AssemblyRegister offsetRegister,
                                  long displacement,
                                  long scale) {
        this.baseRegister = baseRegister;
        this.offsetRegister = offsetRegister;
        this.displacement = displacement;
        this.scale = scale;
    }

    @Override
    public String toString() {
        //TODO: Somebody who know this syntax should do this
        return null;
    }
}
