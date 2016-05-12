package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NullType extends VariableType {

    @Override
    public boolean isNullable() {
        return true;
    }
}
