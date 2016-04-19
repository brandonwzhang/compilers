package com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.Identifier;

import java.util.HashMap;

public class Context extends HashMap<Identifier, Type> {

    public Context() {
        super();
    }

    public Context(Context c) {
        super(c);
    }
}
