package com.bwz6jk2227esl89ahj34;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by jihunkim on 2/6/16.
 */
public abstract class Option {
    String option;
    String description;
    public Option() {
        option = null;
        description = null;
    }

    public Option(String option, String description) {
        this.option = option;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract void action(String[] args);
}
