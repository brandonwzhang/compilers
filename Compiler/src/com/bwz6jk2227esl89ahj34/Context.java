package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Identifier;
import com.bwz6jk2227esl89ahj34.AST.Type;

import java.util.HashMap;

public class Context extends HashMap<Identifier, Type> {

    public Context() {
        super();
    }

    public Context(Context c) {
        super(c);
    }
}
