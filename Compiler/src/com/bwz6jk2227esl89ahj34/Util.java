package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.Program;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * A class containing static utility methods and variables.
 */
public class Util {
    /**
     * Translates symbol numbers into print-friendly strings. Used for lexer
     * and parser outputting.
     */
    public static final HashMap<Integer, String> symbolTranslation =
            new HashMap<Integer, String>() {{
        put(ParserSym.IF, "if");
        put(ParserSym.WHILE, "while");
        put(ParserSym.ELSE, "else");
        put(ParserSym.RETURN, "return");
        put(ParserSym.LENGTH, "length");
        put(ParserSym.INT, "int");
        put(ParserSym.BOOL, "bool");
        put(ParserSym.TRUE, "true");
        put(ParserSym.FALSE, "false");
        put(ParserSym.IDENTIFIER, "id");
        put(ParserSym.INTEGER_LITERAL, "integer");
        put(ParserSym.NOT, "!");
        put(ParserSym.TIMES, "*");
        put(ParserSym.HIGH_MULT, "*>>");
        put(ParserSym.DIVIDE, "/");
        put(ParserSym.MODULO, "%");
        put(ParserSym.PLUS, "+");
        put(ParserSym.MINUS, "-");
        put(ParserSym.LT, "<");
        put(ParserSym.LEQ, "<=");
        put(ParserSym.GEQ, ">=");
        put(ParserSym.GT, ">");
        put(ParserSym.EQUAL, "==");
        put(ParserSym.NOT_EQUAL, "!=");
        put(ParserSym.AND, "&");
        put(ParserSym.OR, "|");
        put(ParserSym.STRING_LITERAL, "string");
        put(ParserSym.EOF, "EOF");
        put(ParserSym.OPEN_PAREN, "(");
        put(ParserSym.CLOSE_PAREN, ")");
        put(ParserSym.OPEN_BRACKET, "[");
        put(ParserSym.CLOSE_BRACKET, "]");
        put(ParserSym.OPEN_BRACE, "{");
        put(ParserSym.CLOSE_BRACE, "}");
        put(ParserSym.COLON, ":");
        put(ParserSym.COMMA, ",");
        put(ParserSym.GETS, "=");
        put(ParserSym.SEMICOLON, ";");
        put(ParserSym.CHARACTER_LITERAL, "character");
        put(ParserSym.USE, "use");
        put(ParserSym.UNDERSCORE, "_");
        put(ParserSym.error, "error:");
    }};

    /**
     * Prints a list of lines into a file.
     * @param file the name of the file
     * @param lines a list of Strings to be printed line by line
     */
    public static void writeAndClose(String file, List<String> lines) {

        if (Main.debugOn()) {
            System.out.println("DEBUG: Writing " + file);
        }

        if(!lines.isEmpty()) {
            try {
                PrintWriter writer = new PrintWriter(file);
                for (int i = 0; i < lines.size() - 1; i++) {
                    writer.println(lines.get(i));
                }
                // Prints last line without a newline at the end
                writer.print(lines.get(lines.size() - 1));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates the directories that make up the given path.
     * Nothing happens if the path already exists.
     * @param path
     */
    public static void makePath(String path) {
        File file = new File(path);
        boolean created = file.mkdirs();

        if (created && Main.debugOn()) {
            System.out.println("Created path: " + path);
        }
    }

    /**
     * A helper function for writing a diagnostic file.
     * Side effect: the necessary directories will be created.
     *
     * @param file The source file's name. Must end in .xi
     * @param extension The extension of the file to be written.
     * @param diagnosticPath The destination path.
     * @param lines The contents to be written.
     */
    public static void writeHelper(String file,
                                   String extension,
                                   String diagnosticPath,
                                   List<String> lines) {
        String output = file.replace(".xi", "." + extension);
        String writeFile = diagnosticPath + output;
        makePath(writeFile.substring(0, writeFile.lastIndexOf('/') + 1));
        writeAndClose(writeFile, lines);
    }

    /**
     * fileName1 and fileName2 are the locations of files that contain
     * S expressions. This method compares those two S expressions for
     * equality. Returns true if they are equal; false otherwise.
     */
    public static boolean compareSExpFiles(String fileName1, String fileName2)
            throws IOException {

        String sExp1 = new String(Files.readAllBytes(Paths.get(fileName1)),
                Charset.forName("UTF-8"));
        String sExp2 = new String(Files.readAllBytes(Paths.get(fileName2)),
                Charset.forName("UTF-8"));

        return compareSExp(sExp1, sExp2);
    }

    /**
     * A method that compares two S expressions for equality. Insignificant
     * whitespace is ignored. Returns true if they are equal; false otherwise.
     */
    public static boolean compareSExp(String sExp1, String sExp2) {
        
        sExp1 = sExp1.replaceAll("\\s+", " ")
                .replaceAll("\\(\\s?", "(")
                .replaceAll("\\s?\\)", ")")
                .trim();

        sExp2 = sExp2.replaceAll("\\s+", " ")
                .replaceAll("\\(\\s?", "(")
                .replaceAll("\\s?\\)", ")")
                .trim();

        System.out.println(sExp1);
        System.out.println(sExp2);

        return sExp1.equals(sExp2);
    }

    /**
     * Creates a FileReader for sourcePath + file and returns it inside
     * an Optional. If the file is not found, then an empty Optional
     * is returned.
     */
    public static Optional<FileReader> getFileReader(String sourcePath,
                                                     String file) {

        if (Main.debugOn()) {
            System.out.println();
            System.out.println("DEBUG: Reading " + sourcePath + file);
        }

        FileReader reader;
        try {
            reader = new FileReader(sourcePath + file);
            return Optional.of(reader);

        } catch (FileNotFoundException e) {
            System.out.println(sourcePath + file + " was not found.");
            return Optional.empty();
        }
    }

    /**
     * Typechecks the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     * libPath is the path where the interface files (.ixi)
     * are found.
     */
    public static void typeCheck(String sourcePath,
                                 String diagnosticPath,
                                 String libPath,
                                 String file) {

        Optional<FileReader> reader = getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);

        String output = file.replace(".xi", ".typed");
        String writeFile = diagnosticPath + output;
        makePath(writeFile.substring(0, writeFile.lastIndexOf('/') + 1));

        List<String> parseLines = new ArrayList<>();
        Optional<Symbol> result = parseHelper(parser, parseLines);
        if (!result.isPresent()) {
            // TODO: syntactic error. what do we do?
            // System printing is already taken care of
            return;
        }

        NodeVisitor visitor =
                new TypeCheckVisitor(file.replace(".xi", ""), libPath);

        // attempt typechecking
        try {
            System.out.println(output);
            ((Program) result.get().value).accept(visitor);
            System.out.println("typed");
        } catch (TypeException e) {
            System.out.println(e.toString());
            //e.printStackTrace();
            //TODO: write diagnostic
        }
    }

    /**
     * A helper function for parsing. Parses with the given parser.
     * Mutates the given list by adding the lines of the output.
     * If there is a syntax error, the given list is cleared and a
     * single String containing the parser's error message is added.
     *
     * If an exception is thrown during parsing or if there is a syntax
     * error, an empty Optional is returned. If the parsing is successful,
     * an Optional containing the result is returned.
     *
     * @param parser a Parser object
     * @param lines a List<String> for storing the output
     * @return an Optional that might contain the result of the parsing
     */
    public static Optional<Symbol> parseHelper(Parser parser,
                                               List<String> lines) {
        Symbol result;
        try {
            result = parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        if (parser.hasSyntaxError) {
            // handle syntax error, output to file
            String errMessage = parser.syntaxErrMessage;
            parser.hasSyntaxError = false;
            parser.syntaxErrMessage = "";
            lines.clear();
            lines.add(errMessage);

            printError("Syntax", lines.get(0));

            return Optional.empty();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer =
                new CodeWriterSExpPrinter(baos);
        NodeVisitor visitor = new PrintVisitor(printer);

        ((Program)(result.value)).accept(visitor);

        printer.flush();
        lines.add(baos.toString());

        return Optional.of(result);
    }

    /**
     * Parses the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     * The output is an S-expression representing the AST of the given program.
     */
    public static void parseFile(String sourcePath,
                                 String diagnosticPath,
                                 String file) {

        Optional<FileReader> reader = getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());
        Parser parser = new Parser(lexer);

        List<String> lines = new ArrayList<>();
        parseHelper(parser, lines);
        writeHelper(file, "parsed", diagnosticPath, lines);
    }

    /**
     * A helper function for lexing. Lexes with the given lexer.
     * Mutates the given list by adding the lines of the output.
     *
     * @param lexer a Lexer object
     * @param lines a List<String> for storing the output
     */
    public static void lexHelper(Lexer lexer, List<String> lines) {
        try {
            Symbol next = lexer.next_token();
            while (next.sym != ParserSym.EOF) {
                String line = next.left + ":" +
                        next.right + " " +
                        Util.symbolTranslation.get(next.sym);
                if (next.value != null) {
                    line += " " + next.value;
                }
                lines.add(line);
                if (next.sym == ParserSym.error) {
                    printError("Lexical", lines.get(lines.size() - 1));
                    break;
                }
                next = lexer.next_token();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lexes the Xi file located at sourcePath + file.
     * Writes the output to diagnosticPath + file.
     */
    public static void lexFile(String sourcePath,
                               String diagnosticPath,
                               String file) {

        Optional<FileReader> reader = getFileReader(sourcePath, file);
        if (!reader.isPresent()) {
            return;
        }

        Lexer lexer = new Lexer(reader.get());

        List<String> lines = new ArrayList<>();
        lexHelper(lexer, lines);
        writeHelper(file, "lexed", diagnosticPath, lines);
    }

    /**
     * Reformats the given error message into:
     * <kind> error beginning at <line>:<column>: <description>
     * Prints this to System.out
     *
     * @param errorType The type of error.
     * @param errorMessage Must be of the form:
     *                     <line>:<column> error:<description>
     */
    public static void printError(String errorType, String errorMessage) {
        int firstColon = errorMessage.indexOf(':');
        int lineNum = Integer.parseInt(errorMessage.substring(0, firstColon));
        int columnNum = Integer.parseInt(errorMessage.substring(
                firstColon + 1, errorMessage.indexOf(' ')));
        String description = errorMessage
                .substring(errorMessage.lastIndexOf(':') + 1);
        System.out.println(errorType + " error: beginning at "
                + lineNum + ":" + columnNum + ":" + description);
    }

    /**
     * Returns a list of file names in a given directory
     * @param directoryPath a String of the path to the directory
     * @return a List<String> of all the file names in the directory
     */
    public static List<String> getDirectoryFiles(String directoryPath) {
        List<String> files = new ArrayList<>();
        try {
            Files.walk(Paths.get(directoryPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String filePathStr = filePath.toString();
                    String file = filePathStr.substring(filePathStr.lastIndexOf('/') + 1, filePathStr.length());
                    files.add(file);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
