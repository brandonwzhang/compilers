package com.bwz6jk2227esl89ahj34.ast.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnitType extends Type {

    @Override
    public boolean isUnit() {
        return true;
    }
}
