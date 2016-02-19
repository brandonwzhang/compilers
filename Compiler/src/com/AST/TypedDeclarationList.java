package com.AST;

import lombok.Data;
import java.util.List;

public @Data class TypedDeclarationList extends Statement {
    private List<TypedDeclaration> typedDeclarationList;
}
