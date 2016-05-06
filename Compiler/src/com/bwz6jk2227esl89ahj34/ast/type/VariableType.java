package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class VariableType extends Type {

    private BaseType baseType;
    private Integer numBrackets;

    public VariableType(BaseType t) {
        this.baseType = t;
        numBrackets = 0;
    }

    public VariableType(BaseType t, Integer numBrackets) {
        this.baseType = t;
        this.numBrackets = numBrackets;
    }

    public static VariableType intArray(int numBrackets) {
        return new VariableType(new IntType(), numBrackets);
    }

    public static VariableType boolArray(int numBrackets) {
        return new VariableType(new BoolType(), numBrackets);
    }
}


