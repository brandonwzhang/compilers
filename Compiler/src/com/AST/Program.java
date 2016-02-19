package com.AST;

import lombok.Data;

import java.util.List;

public @Data class Program extends Node {
    private List<UseStatement> useBlock;
    private List<FunctionDeclaration> funcDecs;
}
