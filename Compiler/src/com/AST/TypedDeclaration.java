package com.AST;

import lombok.Data;
import java.util.List;

public @Data class TypedDeclaration extends Statement implements Assignable {
    Identifier id;
    Type type;
}