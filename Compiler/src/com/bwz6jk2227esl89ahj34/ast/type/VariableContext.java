package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;

import java.util.LinkedHashMap;

public class VariableContext extends LinkedHashMap<Identifier, Type> {

    public VariableContext() {
        super();
    }

    public VariableContext(VariableContext c) {
        super(c);
    }
}
