package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class VariableType extends Type {
    /**
     * Helper function used in parsing
     */
    public static VariableType construct(BaseType baseType, int numBrackets) {
        if (numBrackets == 0) {
            return baseType;
        }
        return new ArrayType(baseType, numBrackets);
    }
}


