package com.bwz6jk2227esl89ahj34;

import java.util.*;

public class CLI {
    public Map<String, Option> options;

    public CLI() {
        // we use a LinkedHashMap because we want to preserve the order
        // in which the options were added
        options = new LinkedHashMap<>();
        Option.Action helpAction = args -> printOptions();
        Option helpOption = new Option("List commands", helpAction, 0);
        options.put("--help", helpOption); // help is a default option
    }

    /**
     * Run the action associated with each option passed
     * @param args a String array of all arguments passed to the command
     */
    public void execute(String[] args) {

        if (args.length == 0) {
            options.get("--help").action.run(args);
            return;
        }

        // get the indices at which the options are located
        Map<String, Integer> optionStartingIndexMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (options.keySet().contains(args[i])) {
                optionStartingIndexMap.put(args[i], i);
            }
        }

        // create the arg lists in the order determined by 'options'
        LinkedHashMap<String, String[]> optionArgsMap = new LinkedHashMap<>();
        for (String optionName : options.keySet()) {

            if (!optionStartingIndexMap.keySet().contains(optionName)) {
                continue; // this option was not included
            }

            Option option = options.get(optionName);
            int i = optionStartingIndexMap.get(optionName) + 1;
            int j; // will become the index of the last argument plus 1
            if (option.nArgs.isPresent()) {
                j = option.nArgs.get() + i;
                boolean b = j < args.length && !options.keySet().contains(args[j]);
                if (j > args.length || b) {
                    System.out.println("Incorrect number of arguments");
                    System.out.println(optionName +
                            " must take " + (j-i) + " argument(s).");
                    return;
                }
            } else {
                j = i + 1;
                while (j < args.length && !options.keySet().contains(args[j])) {
                    j++;
                }
            }
            String[] optionArgs = Arrays.copyOfRange(args, i, j);
            optionArgsMap.put(optionName, optionArgs);
        }

        for (String optionName : optionArgsMap.keySet()) {
            Option option = options.get(optionName);
            String[] optionArgs = optionArgsMap.get(optionName);
            option.action.run(optionArgs);
        }
    }

    /**
     * Print all of the options and their descriptions
     */
    public void printOptions() {
        for (String option : options.keySet()) {
            System.out.println(option + "\t\t" +
                    options.get(option).description);
        }
    }

    /**
     * Add an option that takes an exact number of arguments.
     * @param optionName  a String of the option's name
     * @param description a String describing the option
     * @param action      a function String[] -> void
     * @param nArgs       a int of the number of arguments the option takes
     */
    public void addOption(String optionName,
                          String description,
                          Option.Action action,
                          int nArgs) {
        Option option = new Option(description, action, nArgs);
        options.put(optionName, option);
    }

    /**
     * Add an option that can take any number of arguments.
     * @param optionName  a String of the option's name
     * @param description a String describing the option
     * @param action      a function String[] -> void
     */
    public void addOption(String optionName,
                          String description,
                          Option.Action action) {
        Option option = new Option(description, action);
        options.put(optionName, option);
    }
}
