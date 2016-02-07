package com.bwz6jk2227esl89ahj34;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by jihunkim on 2/6/16.
 */

public class Option {
    public String description;
    public Action action;

    public interface Action {
        void run(String[] args);
    }

    public Option() {
        description = null;
        action = null;
    }

    public Option(String description, Action action) {
        this.description = description;
        this.action = action;
    }
}
