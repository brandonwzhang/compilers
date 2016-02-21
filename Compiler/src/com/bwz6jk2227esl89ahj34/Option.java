package com.bwz6jk2227esl89ahj34;

import java.util.Optional;

public class Option {
    public String description;
    public Action action;
    public Optional<Integer> nArgs;

    public interface Action {
        void run(String[] args);
    }

    private Option(String description, Action action, Optional<Integer> nArgs) {
        this.description = description;
        this.action = action;
        this.nArgs = nArgs;
    }

    public Option(String description, Action action, int nArgs) {
        this(description, action, Optional.of(nArgs));
    }

    public Option(String description, Action action) {
        this(description, action, Optional.empty());
    }
}
