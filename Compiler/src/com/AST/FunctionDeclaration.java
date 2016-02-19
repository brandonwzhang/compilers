package com.AST;

import java.util.List;

public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private List<TypedDeclaration> typedDeclarationList;
    private List<Type> typeList;
    private FunctionBlock functionBlock;
}
