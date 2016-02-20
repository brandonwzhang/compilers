package com.bwz6jk2227esl89ahj34;
import com.AST.*;
import java.io.*;
import java_cup.runtime.*;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.addOption("--lex",
                      "Lex a .xi file to a .lexed file",
                      files -> Lexer.lexFile(sourcePath, diagnosticPath,
                              files),
                      1);
        cli.addOption("--parse",
                      "Parse a .xi file to a .parsed file",
                      files -> Main.parse(sourcePath, diagnosticPath, files),
                      1);
        cli.addOption("-sourcepath",
                      "Set the path for source files",
                      Main::setSourcePath,
                      1);
        cli.addOption("-D",
                      "Set the path for diagnostic files",
                      Main::setDiagnosticPath,
                      1);
        cli.execute(args);
    }

    /**
     * Sets the path for source files
     * @param args single element String array containing the path
     */
    public static void setSourcePath(String[] args) {
        if (args[0] == null) {
            System.out.println("Please provide source path");
            return;
        }
        sourcePath = args[0];
    }

    /**
     * Sets the path for diagnostic files
     * @param args single element String array containing the path
     */
    public static void setDiagnosticPath(String[] args) {
        if (args[0] == null) {
            System.out.println("Please provide diagnostic path");
            return;
        }
        diagnosticPath = args[0];
    }

    public static void parse(String sourcePath, String diagnosticPath, String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify input files");
            return;
        }
        if (args[0] == null) {
            System.out.println("Please specify input files");
            return;
        }
        String[] files = args[0].split(":");
        for (int i = 0; i < files.length; i++) {
            files[i] = sourcePath + files[i];
        }
        try {
            for (int i = 0; i < files.length; i++) {
                FileReader reader = new FileReader(files[i]);
                Lexer lexer = new Lexer(reader);
                ComplexSymbolFactory csf = new ComplexSymbolFactory();
                Parser Parser = new Parser(lexer, csf);
                Symbol result = Parser.parse();

                NodeVisitor visitor = new NodeVisitor(); // TODO: pass vars
                ((Program)(result.value)).accept(visitor);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
