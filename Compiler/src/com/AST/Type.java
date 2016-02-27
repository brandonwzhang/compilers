package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class Type {

    public enum PrimitiveType {
        INT, BOOL, UNIT, VOID;

        @Override public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private PrimitiveType primitiveType;
    private Integer numBrackets;

    public Type(PrimitiveType t, Integer numBrackets) {
        this.primitiveType = t;
        this.numBrackets = numBrackets;

        if(primitiveType == PrimitiveType.UNIT || primitiveType == PrimitiveType.VOID) {
            assert this.numBrackets == 0;
        }
    }

}


