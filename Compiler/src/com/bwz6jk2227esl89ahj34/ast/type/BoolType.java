package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BoolType extends PrimitiveType {
    public boolean isBool() {
        return true;
    }

    @Override
    public String toString() {
        return "bool";
    }
}
