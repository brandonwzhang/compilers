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
                files -> Parser.parseFile(sourcePath, diagnosticPath, files)
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

    public static void testHarness() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
        }

        Parser.parseFile("../parser/tests/", "../parser/tests/", testFileNames);

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
