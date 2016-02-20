package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private List<TypedDeclaration> typedDeclarationList;
    private List<Type> typeList;
    private FunctionBlock functionBlock;
}