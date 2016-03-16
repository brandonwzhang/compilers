package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Program;
import com.bwz6jk2227esl89ahj34.util.CodeWriterSExpPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by andy on 3/15/16.
 */
public class Tests {
    /**
     * Automated tests for typecheck.
     */
    public static void typeCheckTests() {

        System.out.println("\n================Typecheck Tests================");

        System.out.println("\n================Passed Tests================");
        Util.getDirectoryFiles("typecheck/passtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> Core.typeCheck("typecheck/passtests/",
                        "typecheck/passtests/diagnostics/",
                        "typecheck/lib/", filename));

        System.out.println("\n================Failed Tests================");
        Util.getDirectoryFiles("typecheck/failtests/").stream()
                .filter(filename -> filename.contains(".xi"))
                .forEach(filename -> Core.typeCheck("typecheck/failtests/",
                        "typecheck/failtests/diagnostics/",
                        "typecheck/lib/", filename));
    }

    /**
     * Executes parseFile on a list of filenames. For debugging purposes only.
     * @throws IOException
     */
    public static void parseTestHarness() throws IOException {
        String[] testFileNames = new String[] {"arrayinit", "arrayinit2",
                "ex1", "ex2", "gcd", "insertionsort", "mdarrays", "ratadd",
                "ratadduse", "spec1", "spec2", "spec3" };

        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
            Core.parseFile("parser/tests/", "parser/tests/", testFileNames[i]);
        }

        for (String file : testFileNames) {
            String sExpFile1 =
                    "parser/tests/" + file.replace(".xi",".parsedsol");
            String sExpFile2 =
                    "parser/tests/" + file.replace(".xi", ".parsed");
            System.out.println();
            System.out.println(Util.compareSExpFiles(sExpFile1, sExpFile2));
        }
    }

    public static void constantFoldTest() throws IOException {
        String [] testFileNames = new String[] {"test"};

        System.out.println();
        System.out.println("==CONSTANT FOLD TESTS==");
        for (int i = 0; i < testFileNames.length; i++) {
            testFileNames[i] = testFileNames[i] + ".xi";
            Optional<Program> program =
                    Core.typeCheck("constantfold/", "constantfold/", "", testFileNames[i]);

            if (!program.isPresent()) {
                System.out.println("type checking failed");
                continue;
            }

            ConstantFoldingVisitor cfv = new ConstantFoldingVisitor();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CodeWriterSExpPrinter printer =
                    new CodeWriterSExpPrinter(baos);
            NodeVisitor visitor = new PrintVisitor(printer);

            program.get().accept(cfv);
            program.get().accept(visitor);
            printer.flush();
            System.out.println(baos.toString().replaceAll("\\s+", " ")
                    .replaceAll("\\(\\s?", "(")
                    .replaceAll("\\s?\\)", ")")
                    .trim());
        }
    }
}
