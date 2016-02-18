package com.bwz6jk2227esl89ahj34;

public class Main {
    private static String sourcePath = "./";
    private static String diagnosticPath = "./";

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.addOption("--lex",
                      "Lex a .xi file to a .lexed file",
                      files -> XiLexer.lexFile(sourcePath, diagnosticPath,
                              files),
                      1);
        cli.addOption("--parse",
                      "Parse a .xi file to a .parsed file",
                      Main::parse,
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

    public static void parse(String[] args) {
        // TODO:
    }
}
