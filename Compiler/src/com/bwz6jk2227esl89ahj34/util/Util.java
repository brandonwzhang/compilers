package com.bwz6jk2227esl89ahj34.util;

import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyComment;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.ast.FunctionDeclaration;
import com.bwz6jk2227esl89ahj34.ast.Identifier;
import com.bwz6jk2227esl89ahj34.ast.parse.Parser;
import com.bwz6jk2227esl89ahj34.ast.type.*;
import com.bwz6jk2227esl89ahj34.ast.MethodDeclaration;
import com.bwz6jk2227esl89ahj34.ast.parse.ParserSym;
import com.bwz6jk2227esl89ahj34.analysis.CFGIR;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.IRSeq;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;
import org.junit.rules.TestName;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * A class containing static utility methods and variables.
 */
public class Util {
    public static String rootPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).
            getParentFile().getParentFile().getParentFile().getParentFile().getParent();

    public static List<String> runCommand(String[] command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process;
        List<String> lines = new LinkedList<>();
        try {
            process = pb.start();
            process.waitFor();
            // Store the standard output of these processes in lines1 and lines2
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine = outputReader.readLine();
            while (nextLine != null) {
                System.out.println(nextLine);
                lines.add(nextLine);
                nextLine = outputReader.readLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception occurred reading stdout of processes");
        }
        return lines;
    }

    /**
     * Translates symbol numbers into print-friendly strings. Used for lexer
     * and parser outputting.
     */
    public static final LinkedHashMap<Integer, String> symbolTranslation =
            new LinkedHashMap<Integer, String>() {{
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
        put(ParserSym.NEGATIVE_INT_BOUND, "negative_int_bound");
        put(ParserSym.LENGTH, "length");
        put(ParserSym.CLASS, "class");
        put(ParserSym.EXTENDS, "extends");
        put(ParserSym.NEW,"new");
                put(ParserSym.PERIOD, ".");
                put(ParserSym.NULL, "null");
                put(ParserSym.THIS, "this");
                put(ParserSym.BREAK, "break");
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
        int extensionIndex = file.lastIndexOf('.');
        String output;
        if (extensionIndex == -1) {
            output = file + "." + extension;
        } else {
            output = file.substring(0, extensionIndex) + "." + extension;
        }
        String writeFile = diagnosticPath + '/' +  output;
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

    /**
     * Given a filename that could look like test1.xi, test2.mir, dir1/dir2/test3.parsed,
     * return the name of the file with no extensions or directories.
     * @param file
     * @return
     */
    public static String extractFileName(String file) {
        String filename = file;
        int lastIndexSlash = filename.lastIndexOf('/');
        if (lastIndexSlash != -1) {
            filename = filename.substring(lastIndexSlash+1);
        }
        int extensionIndex = filename.lastIndexOf('.');

        filename = filename.substring(0, extensionIndex);
        return filename;
    }

    /**
     * Writes the given IR tree as an S-Expression to a file.
     *
     * diagnosticPath is the destination directory. The .xi
     * extension will be replaced by the given extension.
     */
    public static void writeIRTree(IRCompUnit root,
                                   String diagnosticPath,
                                   String file,
                                   String extension) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(baos);
        root.printSExp(printer);
        printer.flush();
        List<String> lines = Collections.singletonList(baos.toString());
        Util.writeHelper(file, extension, diagnosticPath, lines);
    }

    public static List<Character> backslashMergedCharList(String str) {
        List<Character> charList = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\' && i != str.length() - 1) {
                switch(str.charAt(i + 1)) {
                    case 'r':
                        charList.add('\r');
                        i++;
                        break;
                    case 'n':
                        charList.add('\n');
                        i++;
                        break;
                    default:
                        charList.add('\\');
                        break;
                }
            } else {
                charList.add(str.charAt(i));
            }
        }

        return charList;
    }

    public static void printInstructions(TestName name, List<AssemblyLine> instructions) {
        System.out.println("==============================\nExecuting "+ name.getMethodName());
        System.out.println();
        for (AssemblyLine instruction : instructions) {
          System.out.println(instruction);
        }
    }

    private static String getBaseTypeString(BaseType type) {
        if (type instanceof IntType) {
            return "i";
        } else if (type instanceof BoolType) {
            return "b";
        } else {
            ClassType classType = (ClassType) type;
            String className = classType.getIdentifier().getName();
            return "o" + className.length() + className;
        }
    }

    private static String getTypeString(VariableType type) {
        if (!(type instanceof ArrayType)) {
            return getBaseTypeString((BaseType) type);
        }

        ArrayType arrayType = (ArrayType) type;
        String typeString = getBaseTypeString(arrayType.getBaseType());
        for (int i = 0; i < arrayType.getNumBrackets(); i++) {
            typeString = "a" + typeString;
        }
        return typeString;
    }

    public static String getIRFunctionName(FunctionDeclaration node) {
        String funcName = node.getIdentifier().getName();
        FunctionType funcType = node.getFunctionType();
        String irName = "_I" + funcName + "_";

        String ret = "";
        List<VariableType> retList = funcType.getReturnTypeList().getVariableTypeList();
        if (retList.size() > 1) {
            ret = "t" + retList.size();
        } else if (retList.size() == 0) {
            ret = "p";
        }
        for (VariableType type : retList) {
            ret += getTypeString(type);
        }

        String arg = "";
        List<VariableType> argList = funcType.getArgTypes();
        for (VariableType type : argList) {
            arg += getTypeString(type);
        }
        irName += ret + arg;
        return irName;
    }

    public static String getIRMethodName(MethodDeclaration node) {
        return node.getClassIdentifier() + "$" + getIRFunctionName(node.getFunctionDeclaration());
    }

    public static List<AssemblyLine> removeComments(List<AssemblyLine> lst) {
       List<AssemblyLine> filteredList = new LinkedList<>();
        for(AssemblyLine assemblyLine : lst) {
           if (! (assemblyLine instanceof AssemblyComment)) {
               filteredList.add(assemblyLine);
           }
       }
        return filteredList;
    }

    public static void writeCFGs(String file, String phase, IRCompUnit root) {
        int dotIndex = file.lastIndexOf('.');
        for (String functionName : root.functions().keySet()) {
            int i = functionName.indexOf('I');
            String afterI = functionName.substring(i + 1);
            String demangled = afterI.substring(0, afterI.indexOf('_'));
            String cfgReportName =
                    file.substring(0, dotIndex) + "_" + demangled + "_" + phase + ".xi";
            IRSeq seq = (IRSeq) root.functions().get(functionName).body();
            CFGIR cfg = new CFGIR(seq);
            List<String> lines = Collections.singletonList(cfg.toString());
            Util.writeHelper(cfgReportName, "dot", Main.diagnosticPath(), lines);
        }
    }
}
