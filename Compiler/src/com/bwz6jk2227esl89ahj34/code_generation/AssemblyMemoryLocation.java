package com.bwz6jk2227esl89ahj34.code_generation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyMemoryLocation extends AssemblyExpression {
    public Long displacement;
    public Long scale;
    public AssemblyRegister offsetRegister;
    public AssemblyRegister baseRegister;

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


    public static AssemblyMemoryLocation stackOffset(int offset) {
        return new AssemblyMemoryLocation(AssemblyPhysicalRegister.RBP, null, -offset);
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
        if (offsetRegister != null) {
            s += ",";
            s += offsetRegister;
        }
        if (scale != null) {
            s += ",";
            s += scale;
        }
        s += ")";

        return s;
    }
}
