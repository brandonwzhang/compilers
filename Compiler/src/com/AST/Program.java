package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@AllArgsConstructor
@Data
public class Program extends Node {
    private List<UseStatement> useBlock;
    private List<FunctionDeclaration> funcDecs;
}
