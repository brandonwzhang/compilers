package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayType extends VariableType {
    private BaseType baseType;
    private Integer numBrackets;

    public ArrayType(BaseType t, Integer numBrackets) {
        assert numBrackets > 0;
        this.baseType = t;
        this.numBrackets = numBrackets;
    }
}
