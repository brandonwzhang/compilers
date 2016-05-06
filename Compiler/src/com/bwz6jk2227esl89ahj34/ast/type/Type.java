package com.bwz6jk2227esl89ahj34.ast.type;


import com.bwz6jk2227esl89ahj34.ast.ClassDeclaration;

public abstract class Type {
    public static final UnitType UNIT = new UnitType();
    public static final VoidType VOID = new VoidType();
    public static final VariableType INT = new VariableType(new IntType(), 0);
    public static final VariableType BOOL = new VariableType(new BoolType(), 0);
}