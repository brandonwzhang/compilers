package com.bwz6jk2227esl89ahj34;
import java.io.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{
        CLI cli = new CLI();
        cli.addOption("--lex", "Lex a string", a -> lex());
        cli.execute(args);

        // get current directory
        File file = new File("");
        FileReader reader = new FileReader(file.getAbsolutePath() + "/src/com/bwz6jk2227esl89ahj34/test.xi");
        XiLexer lexer = new XiLexer(reader);

        try {
            while(true) {
                System.out.println(lexer.next_token());
            }
        } catch (Exception e) {

        }
    }

    public static void lex() {
        System.out.println("Lexing is not yet implemented");
    }

}
