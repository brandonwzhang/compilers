package com.bwz6jk2227esl89ahj34;

public class Main {

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.addOption("--lex", "Lex a string", a -> lex());
        cli.execute(args);

        
    }

    public static void lex() {
        System.out.println("Lexing is not yet implemented");
    }

}
