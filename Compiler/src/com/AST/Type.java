package com.AST;

import lombok.Data;
import java.util.List;

public @Data class Type extends Node {

    public enum PrimitiveType {
        INT,
        BOOL
    }

    PrimitiveType basicType;

    ArrayBrackets arrayBrackets;
}
