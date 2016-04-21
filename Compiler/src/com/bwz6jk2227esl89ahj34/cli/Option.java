package com.bwz6jk2227esl89ahj34.cli;

public class Option {
    public String description;
    public Action action;
    public int nArgs;

    public interface Action {
        void run(String[] args);
    }

    public Option(String description, Action action, int nArgs) {
        this.description = description;
        this.action = action;
        this.nArgs = nArgs;
        assert nArgs >= 0;
    }
}
