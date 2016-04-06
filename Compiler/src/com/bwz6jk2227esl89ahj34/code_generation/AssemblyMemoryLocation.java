package com.bwz6jk2227esl89ahj34.code_generation;

public class AssemblyMemoryLocation extends AssemblyExpression {
    private Long displacement;
    private Long scale;
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
        // syntax is displacement(base register, offset register, scalar multiplier)
        String s = "";
        if (displacement != null) {
            s += displacement;
        }
        s += "(";

        if (baseRegister != null) {
            s += baseRegister;
        }
        s += ",";
        if (offsetRegister != null) {
            s += offsetRegister;
        }
        s += ",";
        if (scale != null) {
            s += scale;
        }
        s += ")";

        return s;
    }
}