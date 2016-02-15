package com.bwz6jk2227esl89ahj34;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class CLI {
    public Map<String, Option> options;

    public CLI() {
        options = new HashMap<>();
        Option.Action helpAction = args -> printOptions();
        Option helpOption = new Option("List commands", helpAction, 0);
        options.put("--help", helpOption);
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
        for (int i = 0; i < args.length; i++) {
            // Return early if an invalid option is detected
            if (!options.keySet().contains(args[i])) {
                System.out.println(args[0] + " is not an option.");
                return;
            }
            Option option = options.get(args[i]);
            // Check if enough arguments were passed to option
            if (args.length < i + option.nArgs + 1) {
                System.out.println("Too few arguments passed to " + args[i]);
                return;
            }
            // Extract the arguments to this function
            String[] optionArgs = Arrays.copyOfRange(args,
                                                     i + 1,
                                                     i + option.nArgs + 1);
            // Pass these options to the option's function
            option.action.run(optionArgs);
            // Jump past the option's argument in the array
            i += option.nArgs;
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
     * Add an option with a single name
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
     * Add an option with multiple names
     * @param optionNames a String[] of the option's names
     * @param description a String describing the option
     * @param action      a function String[] -> void
     * @param nArgs       a int of the number of arguments the option takes
     */
    public void addOption(String[] optionNames,
                          String description,
                          Option.Action action,
                          int nArgs) {
        Option option = new Option(description, action, nArgs);
        for (String optionName : optionNames) {
            options.put(optionName, option);
        }
    }
}
