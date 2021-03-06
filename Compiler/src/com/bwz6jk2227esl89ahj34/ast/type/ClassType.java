package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassType extends BaseType {
    private Identifier identifier;

    @Override
    public boolean isNullable() {
        return true;
    }
}
