package com.AST;

import lombok.Data;
import java.util.List;

public @Data class TypedDeclaration extends Node {
    Identifier id;
    Type type;
}