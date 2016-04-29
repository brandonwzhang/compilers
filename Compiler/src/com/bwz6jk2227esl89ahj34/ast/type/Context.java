package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;

import java.util.LinkedHashMap;

public class Context extends LinkedHashMap<Identifier, Type> {

    public Context() {
        super();
    }

    public Context(Context c) {
        super(c);
    }
}
