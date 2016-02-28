package com.AST;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class VariableType extends Type {

    private PrimitiveType primitiveType;
    private Integer numBrackets;

    public VariableType(PrimitiveType t, Integer numBrackets) {
        this.primitiveType = t;
        this.numBrackets = numBrackets;

        if(primitiveType == PrimitiveType.UNIT || primitiveType == PrimitiveType.VOID) {
            assert this.numBrackets == 0;
        }
    }

}


