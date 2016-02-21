package com.bwz6jk2227esl89ahj34;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Util {
    public static void writeAndClose(String file, ArrayList<String> lines) {
        try {
            PrintWriter writer = new PrintWriter(file);
            for (int i = 0; i < lines.size() - 1; i++) {
                writer.println(lines.get(i));
                i++;
            }
            writer.print(lines.get(lines.size() - 1));
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean compareSExpFiles(String fileName1, String fileName2) throws IOException {

        String sExp1 = new String(Files.readAllBytes(Paths.get(fileName1)),
                Charset.forName("UTF-8"));
        String sExp2 = new String(Files.readAllBytes(Paths.get(fileName2)),
                Charset.forName("UTF-8"));

        return compareSExp(sExp1, sExp2);
    }

    public static boolean compareSExp(String sExp1, String sExp2) {
        
        sExp1 = sExp1.replaceAll("\\s+", " ")
                .replaceAll("\\(\\s?", "(")
                .replaceAll("\\s?\\)", ")");

        sExp2 = sExp2.replaceAll("\\s+", " ")
                .replaceAll("\\(\\s?", "(")
                .replaceAll("\\s?\\)", ")");

        System.out.println(sExp1);
        System.out.println(sExp2);

        return sExp1.trim().equals(sExp2.trim());
    }
}
