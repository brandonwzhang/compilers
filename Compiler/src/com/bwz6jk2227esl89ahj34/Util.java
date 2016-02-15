package com.bwz6jk2227esl89ahj34;

import java.io.PrintWriter;
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
}
