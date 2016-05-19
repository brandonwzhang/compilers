package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;

import java.util.LinkedHashMap;

public class FunctionContext extends LinkedHashMap<Identifier, Type> {

    public FunctionContext() {
        super();
    }

    public FunctionContext(FunctionContext c) {
        super(c);
    }
}