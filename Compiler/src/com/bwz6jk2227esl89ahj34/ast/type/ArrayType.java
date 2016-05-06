package com.bwz6jk2227esl89ahj34.ast.type;

public class ArrayType extends VariableType {
    private BaseType baseType;
    private Integer numBrackets;

    public ArrayType(BaseType t) {
        this.baseType = t;
        numBrackets = 0;
    }

    public ArrayType(BaseType t, Integer numBrackets) {
        this.baseType = t;
        this.numBrackets = numBrackets;
    }

    public static final ArrayType INT = new ArrayType(new IntType(), 0);
    public static final ArrayType BOOL = new ArrayType(new BoolType(), 0);

    public static ArrayType intArray(int numBrackets) {
        return new ArrayType(new IntType(), numBrackets);
    }

    public static ArrayType boolArray(int numBrackets) {
        return new ArrayType(new BoolType(), numBrackets);
    }
}
