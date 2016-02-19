package com.AST;

import lombok.Data;

import java.util.List;

public @Data class FunctionDeclaration extends Node {
    private Identifier identifier;
    private List<TypedDeclaration> typedDeclarationList;
    private List<Type> typeList;
    private Block.FunctionBlock functionBlock;
}
