package com.bwz6jk2227esl89ahj34.AST.type;

import com.bwz6jk2227esl89ahj34.AST.Identifier;

import java.util.HashMap;

public class Context extends HashMap<Identifier, Type> {

    public Context() {
        super();
    }

    public Context(Context c) {
        super(c);
    }
}
