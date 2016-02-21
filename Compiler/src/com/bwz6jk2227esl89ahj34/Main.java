package com.bwz6jk2227esl89ahj34;
import com.AST.*;
import java.io.*;
import java_cup.runtime.*;
import edu.cornell.cs.cs4120.util.*;

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
        try {
            for (String file : files) {
                FileReader reader = new FileReader(sourcePath + file);
                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                Symbol result = parser.parse();

                if (!file.contains(".xi")) {
                    System.out.println(file + "is not a .xi file. This file will not be parsed.");
                    continue;
                }
                String output = file.replace(".xi", ".parsed");

                FileOutputStream fos = new FileOutputStream(new File(diagnosticPath + output));
                CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(fos);
                NodeVisitor visitor = new PrintVisitor(printer);
                ((Program)(result.value)).accept(visitor);
                printer.flush();

                //boolean b = Util.compareSExpFiles(sourcePath+"giventest2_expected.parsed", diagnosticPath+output);
                //System.out.println(b);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

//        try {
//            System.out.println();
//            System.out.println(Util.compareSExp("../parser/tests/test2.parsed", "../parser/tests/test2.parsed"));
//        } catch(Exception e) {
//
//        }
    }
}
