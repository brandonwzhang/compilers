package com.bwz6jk2227esl89ahj34;
import com.AST.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import java_cup.runtime.*;
import edu.cornell.cs.cs4120.util.*;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";

    public static void main(String[] args) {
        CLI cli = new CLI();
        // the order in which options are added is the order in which they
        // are executed (but options can be provided in any order when running this file)
        cli.addOption("-sourcepath",
                      "Set the path for source files",
                      Main::setSourcePath,
                      1);
        cli.addOption("-D",
                      "Set the path for diagnostic files",
                      Main::setDiagnosticPath,
                      1);
        cli.addOption("--lex",
                "Lex a .xi file to a .lexed file",
                files -> Lexer.lexFile(sourcePath, diagnosticPath,
                        files)
        );
        cli.addOption("--parse",
                "Parse a .xi file to a .parsed file",
                files -> Main.parse(sourcePath, diagnosticPath, files)
        );
        cli.execute(args);
        try {
            //testHarness();
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    public static void parse(String sourcePath,
                             String diagnosticPath,
                             String[] files) {
        try {
            for (String file : files) {
                if (!file.contains(".xi")) {
                    System.out.println(file + "is not a .xi file. " +
                            "This file will not be parsed.");
                    continue;
                }
                System.out.println("Parsing " + sourcePath + file);

                FileReader reader = new FileReader(sourcePath + file);
                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);

                String output = file.replace(".xi", ".parsed");

                Symbol result = parser.parse();

                if (parser.hasSyntaxError) {
                    // handle syntax error, output to file
                    parser.hasSyntaxError = false;
                    Util.writeAndClose(diagnosticPath + output, new
                            ArrayList<String>(Arrays.asList(parser.syntaxErrMessage)));

                    parser.syntaxErrMessage = "";
                    continue;
                }


                FileOutputStream fos = new FileOutputStream(
                        new File(diagnosticPath + output));
                CodeWriterSExpPrinter printer =
                        new CodeWriterSExpPrinter(fos);
                NodeVisitor visitor = new PrintVisitor(printer);

                ((Program)(result.value)).accept(visitor);
                printer.flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void testHarness() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
        }

        parse("../parser/tests/", "../parser/tests/", testFileNames);

        for (String file : testFileNames) {
            String sExpFile1 =
                    "../parser/tests/" + file.replace(".xi",".parsedsol");
            String sExpFile2 =
                    "../parser/tests/" + file.replace(".xi", ".parsed");
            System.out.println();
            System.out.println(Util.compareSExpFiles(sExpFile1, sExpFile2));
        }
    }
}
