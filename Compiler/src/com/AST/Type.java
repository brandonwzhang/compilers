package com.AST;

import lombok.Data;
import java.util.List;

public @Data class Type extends Node {

    enum BasicType {
        INT,
        BOOL
    }

    BasicType basicType;

    // the expressions that go inside the front brackets
    List<Expression> sizedBrackets;

    // the number of emptyBrackets
    int emptyBrackets;
}
