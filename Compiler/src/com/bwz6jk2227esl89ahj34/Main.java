package com.bwz6jk2227esl89ahj34;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static Map<Integer, String> symbolTranslation;

    public static void main(String[] args) throws FileNotFoundException{
        CLI cli = new CLI();
        cli.addOption("--lex", "Lex a file", Main::lex);
        cli.addOption("--parse", "Parse a .xi file to a .parsed file",
                Main::parse);
        // TODO: -sourcepath, -D
        cli.execute(args);
    }

    public static void lex(String[] args) {
        if(args.length == 0) {
            System.out.println("Please specify input file.");
            return;
        }
        try {
            for (int i = 0; i < args.length; i++) {
                ArrayList<String> lines = new ArrayList<>();
                PrintWriter writer = new PrintWriter(args[i].substring(0,
                        args[i].indexOf(".")) + ".lexed");
                FileReader reader = new FileReader(args[i]);
                XiLexer lexer = new XiLexer(reader);
                StringBuilder sb;
                while (true) {
                    java_cup.runtime.Symbol next = lexer.next_token();
                    if (next.sym == sym.EOF) {
                        writeAndClose(writer, lines);
                        break;
                    }
                    sb = new StringBuilder((next.left + 1) + ":" +
                            (next.right + 1) + " " +
                            XiLexer.symbolTranslation.get(next.sym));
                    if (next.value != null) {
                        sb.append(" " + next.value);
                    }
                    lines.add(sb.toString());

                    if (next.sym == sym.error) {
                        writeAndClose(writer, lines);
                        break;
                    }
                }
                writer.close();
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
    }

    public static void parse(String[] args) {
        // TODO:
    }

    public static void writeAndClose(PrintWriter writer, ArrayList<String> lines) {
        int i = 0;
        while ( i < lines.size() - 1) {
            writer.println(lines.get(i));
            i++;
        }
        writer.print(lines.get(i));
        writer.close();
    }

}
