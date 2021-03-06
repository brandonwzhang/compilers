package com.bwz6jk2227esl89ahj34.cli;

import com.bwz6jk2227esl89ahj34.Core;
import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.optimization.OptimizationType;

import java.util.*;

/**
 * A class that implements the command line interface for xic.
 *
 * --help is an option that is included by default. It prints a
 * summary of all the available options.
 *
 * The names of options must begin with the character '-'.
 *
 * Any argument that is not associated with an option is
 * taken to be a source file.
 *
 * Options that begin with "--" (besides --help) are assumed to
 * operate on the source files. When adding such an option, you
 * should set the number of arguments to 0, but this is not
 * actually enforced by CLI.
 *
 * The options are executed in the order in which they are added.
 * This is so we can ensure that certain options are executed
 * first regardless of what order the options are passed in to
 * the script.
 */
public class CLI {
    private Map<String, Option> options;
    private ArrayList<String> files;

    private static Set<String> validOpts = new LinkedHashSet<>();

    static {
        for (OptimizationType opt : OptimizationType.values()) {
            validOpts.add(opt.toString());
        }
    }

    public CLI() {
        // we use a LinkedLinkedHashMap because we want to preserve the order
        // in which the options were added
        options = new LinkedHashMap<>();
        files = new ArrayList<>();
        Option.Action helpAction = args -> printOptions();
        Option helpOption = new Option("List commands", helpAction, 0);
        options.put("--help", helpOption); // help is a default option
    }

    /**
     * Run the action associated with each option passed
     * @param args a String array of all arguments passed to the command
     */
    public void execute(String[] args) {

        Map<String, String[]> optionArgsMap = new LinkedHashMap<>();

        Set<OptimizationType> enable = new LinkedHashSet<>();
        Set<OptimizationType> disable = new LinkedHashSet<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                String optionName = args[i];

                if (args[i].equals("--report-opts")) {
                    for (OptimizationType opt : OptimizationType.SUPPORTED_OPTIMIZATIONS) {
                        System.out.println(opt.toString());
                    }
                    return;
                }

                if (args[i].length() > 2 && args[i].charAt(1) == 'O') {
                    if (args[i].length() > 6 && args[i].substring(2, 6).equals("-no-")) {
                        // -O-no-<opt>
                        String opt_ = args[i].substring(6);
                        if (validOpts.contains(opt_)) {
                            OptimizationType opt = OptimizationType.valueOf(opt_.toUpperCase());
                            disable.add(opt);
                            Main.allOptimizations = true;
                            continue;
                        }
                    } else {
                        // -O<opt>
                        String opt_ = args[i].substring(2);
                        if (validOpts.contains(opt_)) {
                            OptimizationType opt = OptimizationType.valueOf(opt_.toUpperCase());
                            enable.add(opt);
                            Main.allOptimizations = false;
                            continue;
                        }
                    }
                }

                if (!options.keySet().contains(optionName)) {
                    System.out.println(args[i] + " is not a valid option");
                    return;
                }

                Option option = options.get(optionName);
                String[] optionArgs = new String[option.nArgs];
                for (int j = 0; j < optionArgs.length; j++) {
                    int n = i + j + 1;
                    if (n >= args.length || args[n].charAt(0) == '-') {
                        System.out.println("Incorrect number of arguments");
                        System.out.println(optionName + " takes " +
                                option.nArgs + " argument(s).");
                        return;
                    }
                    optionArgs[j] = args[n];
                }
                i += option.nArgs;

                optionArgsMap.put(optionName, optionArgs);
            } else {
                files.add(args[i]);
            }
        }

        if (!enable.isEmpty() && !disable.isEmpty()) {
            System.out.println("-O<opt> and -O-no-<opt> cannot be used at the same time.");
            return;
        }

        String[] filesArray = files.toArray(new String[files.size()]);

        // options with two dashes operate on files
        for (String optionName : optionArgsMap.keySet()) {
            if (optionName.contains("--")) {
                if (optionName.equals("--optcfg") || optionName.equals("--optir")) {
                    // these two options have two dashes but do not operate on files
                    continue;
                }
                optionArgsMap.put(optionName, filesArray);
            }
        }

        // iterate over 'options' to enforce the order of execution
        for (String optionName : options.keySet()) {
            if (optionArgsMap.containsKey(optionName)) {
                Option option = options.get(optionName);
                String[] optionArgs = optionArgsMap.get(optionName);
                option.action.run(optionArgs);
            }
        }

        if (!Main.target().equals("linux") && !Main.target().equals("macos")) {
            System.out.println("\nError: target operating system must be linux or macos.");
            return;
        }

        Main.optimizationMap.clear();
        for (OptimizationType opt : enable) {
            Main.optimizationMap.put(opt, true);
        }
        for (OptimizationType opt : disable) {
            Main.optimizationMap.put(opt, false);
        }
        for (OptimizationType opt : OptimizationType.values()) {
            if (!Main.optimizationMap.containsKey(opt)) {
                // fill the rest of the map
                Main.optimizationMap.put(opt, Main.allOptimizations);
            }
        }

        // Executable generation
        if (Main.generateExecutable()) {
            Core.generateExecutable(filesArray);
        }
    }

    /**
     * Print all of the options and their descriptions
     */
    public void printOptions() {
        int maxOptionLenth = 0;
        for (String option : options.keySet()) {
            if (option.length() > maxOptionLenth) {
                maxOptionLenth = option.length();
            }
        }
        for (String option : options.keySet()) {
            String line = String.format("%-" + (maxOptionLenth + 4) + "s %s",
                    option, options.get(option).description);
            System.out.println(line);
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
        if (optionName.charAt(0) != '-') {
            System.out.println("The option name must begin with '-'." +
                    "This option will not be added.");
            return;
        }
        Option option = new Option(description, action, nArgs);
        options.put(optionName, option);
    }
}
