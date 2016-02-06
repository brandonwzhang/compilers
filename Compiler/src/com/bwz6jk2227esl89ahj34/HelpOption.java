package com.bwz6jk2227esl89ahj34;

import java.util.ArrayList;

/**
 * Created by jihunkim on 2/6/16.
 */
public class HelpOption extends Option {
    public HelpOption() {
        super("--help", "Print a synopsis of options");
        CLI.options.put(this.option, this);
    }
    public void action(String[] args) {
        CLI.printOptions();
    }
}
