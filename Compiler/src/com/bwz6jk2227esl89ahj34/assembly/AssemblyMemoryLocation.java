package com.bwz6jk2227esl89ahj34.assembly;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblyMemoryLocation extends AssemblyExpression {
    public long displacement = 0;
    public long scale = 1;
    public AssemblyRegister offsetRegister;
    public AssemblyRegister baseRegister;
    public AssemblyName offsetName;

    public AssemblyMemoryLocation(AssemblyRegister baseRegister) {
        this.baseRegister = baseRegister;
    }

    public AssemblyMemoryLocation(AssemblyRegister baseRegister,
                                  long displacement) {
        this.baseRegister = baseRegister;
        this.displacement = displacement;
    }

    public AssemblyMemoryLocation(AssemblyRegister baseRegister,
                                  AssemblyName offsetName) {
        this.baseRegister = baseRegister;
        this.offsetName = offsetName;
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
        assert !((displacement == 0) && (offsetName == null)); // can't have both available

        // syntax is displacement(base register, offset register, scalar multiplier)
        String s = "";
        if (displacement != 0) {
            s += displacement;
        }
        if (offsetName != null) {
            s += offsetName.getName();
        }
        s += "(";

        if (baseRegister != null) {
            s += baseRegister;
        }
        if (offsetRegister != null) {
            s += ",";
            s += offsetRegister;
        }
        if (scale != 1) {
            s += ",";
            s += scale;
        }
        s += ")";

        return s;
    }
}
