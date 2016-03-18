package com.bwz6jk2227esl89ahj34.util;

import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.AST.parse.ParserSym;

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
                + lineNum + ":" + columnNum + ": " + description);
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