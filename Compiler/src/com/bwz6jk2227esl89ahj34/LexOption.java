package com.bwz6jk2227esl89ahj34;

/**
 * Created by jihunkim on 2/6/16.
 */
public class LexOption extends Option {
    public LexOption() {
        super("--lex", "Generate output from lexical analysis. The inputs for lexical analysis will be the source files given in the arguments");
        CLI.options.put(this.option, this);
    }
    public void action(String[] args) {
        if(args.length == 0) {
            System.out.println("Please provide input files to perform lexical analysis on as arguments");
        } else {
            System.out.println("TODO");
        }
    }
}
