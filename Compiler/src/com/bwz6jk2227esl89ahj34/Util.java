package com.bwz6jk2227esl89ahj34;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class containing static utility methods and variables.
 */
public class Util {
    /**
     * Translates symbol numbers into print-friendly strings. Used for lexer
     * and parser outputting.
     */
    public static final HashMap<Integer, String> symbolTranslation = new HashMap<Integer, String>(){{
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
    public static void writeAndClose(String file, ArrayList<String> lines) {
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
        file.mkdirs();
    }

    /**
     * fileName1 and fileName2 are the locations of files that contain
     * S expressions. This method compares those two S expressions for
     * equality. Returns true if they are equal; false otherwise.
     */
    public static boolean compareSExpFiles(String fileName1, String fileName2) throws IOException {

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
}
