package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class IntType extends PrimitiveType {

    public boolean isInt() {
        return true;
    }

    public boolean isBool() {
        return true;
    }
}
