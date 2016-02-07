package com.bwz6jk2227esl89ahj34;

import java.util.Map;
import java.util.HashMap;
/**
 * Created by jihunkim on 2/6/16.
 */
public class CLI {
    public static Map<String, Option> options;

    public CLI() {
        options = new HashMap<>();
    }

    public static void execute(String[] args) {
        if(args.length == 0) {
            options.get("--help").action(args);
        } else {
          options.get(args[0]).action(args);
        }
    }

    public static void printOptions() {
        for(String option : options.keySet()) {
            System.out.println(option + "\t\t"+ options.get(option).getDescription());
        }
    }




}
