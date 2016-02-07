package com.bwz6jk2227esl89ahj34;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by jihunkim on 2/6/16.
 */
public class CLI {
    public Map<String, Option> options;

    public CLI() {
        options = new HashMap<>();
        Option.Action helpAction = args -> printOptions();
        Option helpOption = new Option("List commands", helpAction);
        options.put("--help", helpOption);
    }

    public void execute(String[] args) {
        if (args.length == 0) {
            options.get("--help").action.run(args);
        } else {
            if (!options.keySet().contains(args[0])) {
                System.out.println(args[0] + " is not an option.");
                return;
            }
            options.get(args[0]).action.run(args);
        }
    }

    public void printOptions() {
        for (String option : options.keySet()) {
            System.out.println(option + "\t\t" +
                    options.get(option).description);
        }
    }

    public void addOption(String optionName,
                          String description,
                          Option.Action action) {
        Option option = new Option(description, action);
        options.put(optionName, option);
    }

    public void addOption(String[] optionNames,
                          String description,
                          Option.Action action) {
        Option option = new Option(description, action);
        for (String optionName : optionNames) {
            options.put(optionName, option);
        }
    }
}
