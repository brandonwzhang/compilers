package com.bwz6jk2227esl89ahj34;

import com.AST.Identifier;
import com.AST.Type;

import java.util.HashMap;

public class Context extends HashMap<Identifier, Type> {

    public Context() {
        super();
    }

    public Context(Context c) {
        super(c);
    }
}
