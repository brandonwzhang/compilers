package com.bwz6jk2227esl89ahj34.assembly;

import com.bwz6jk2227esl89ahj34.ir.DataSegment;
import com.bwz6jk2227esl89ahj34.ir.IRCompUnit;
import com.bwz6jk2227esl89ahj34.ir.IRFuncDecl;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssemblyProgram {
    private IRCompUnit compUnit;
    private List<AssemblyFunction> functions = new ArrayList<>();
    // Holds the names of functions in the interfaces used by this program
    private List<String> global;
    private String target;


    /**
     * Generate assembly code for a program
     * @param root IRCompUnit of program
     */
    public AssemblyProgram(IRCompUnit root, List<String> global, String target) {
        compUnit = root;
        this.global = global;
        this.target = target;

        // Get the maximum number of return values and arguments in all functions
        for (String functionName : root.functions().keySet()) {
            if (functionName.startsWith("_I_init")) {
                continue;
            }
            AssemblyFunction.maxNumReturnValues =
                    Math.max(AssemblyFunction.maxNumReturnValues, numReturnValues(functionName));
            AssemblyFunction.maxNumArguments =
                    Math.max(AssemblyFunction.maxNumArguments, numArguments(functionName));
        }

        // Store all the functions
        for (IRFuncDecl funcDecl : root.functions().values()) {
            functions.add(new AssemblyFunction(funcDecl));
        }
    }

    /**
     * Returns the number of arguments for a given function
     */
    public static int numArguments(String functionName) {
        int numArguments = 0;
        int lastUnderscore = functionName.lastIndexOf('_');
        String types = functionName.substring(lastUnderscore + 1);
        int i = 0;

        if (types.charAt(0) == 'p') {
            // example: main(args: int[][]) -> _Imain_paai
            i = 1;

        } else if (types.charAt(0) == 't') {
            // example: parseInt(str: int[]): int, bool -> _IparseInt_t2ibai
            i = 1;
            int numReturnValues = 0;
            while (Character.isDigit(types.charAt(i))) {
                // think carefully
                numReturnValues *= 10;
                numReturnValues += Integer.parseInt("" + types.charAt(i));
                i++;
            }

            // preemptively compensate for the # of return values
            numArguments -= numReturnValues;

        } else {
            // example: unparseInt(n: int): int[] -> _IunparseInt_aii

            // see above
            numArguments--;
        }

        // adds up number of arguments and number of return values
        while (i < types.length()) {
            while (i < types.length() && types.charAt(i) == 'a') {
                i++;
            }
            numArguments++;

            if (types.charAt(i) == 'o') {
                i++;
                int classNameLength = 0;
                while (Character.isDigit(types.charAt(i))) {
                    classNameLength *= 10;
                    classNameLength += Integer.parseInt("" + types.charAt(i));
                    i++;
                }
                i += classNameLength;

            } else {
                i++;
            }
        }

        return numArguments;
    }

    /**
     * Returns the number of return values for a given function
     */
    public static int numReturnValues(String functionName) {
        int numReturnValues;
        int lastUnderscore = functionName.lastIndexOf('_');
        String returnTypes = functionName.substring(lastUnderscore + 1);

        if (returnTypes.charAt(0) == 'p') {
            // example: main(args: int[][]) -> _Imain_paai
            numReturnValues = 0;

        } else if (!(returnTypes.charAt(0) == 't')) {
            // example: unparseInt(n: int): int[] -> _IunparseInt_aii
            numReturnValues = 1;

        } else {
            // example: parseInt(str: int[]): int, bool -> _IparseInt_t2ibai
            int i = 1;
            while (Character.isDigit(returnTypes.charAt(i))) {
                i++;
            }
            numReturnValues = Integer.parseInt(returnTypes.substring(1, i));
        }
        return numReturnValues;
    }

    @Override
    public String toString() {
        String s = "";
        // Add the data segment
        s += compUnit.data();
        // Add the functions
        s += "\t\t.text\n";
        for (AssemblyFunction function : functions) {
            for(String globalName : global) {
                s+= "\t\t.globl\t" + globalName + "\n";
            }
            s += function + "\n";
        }
        // Add the .ctors section
        if (target.equals("linux")) {
            s += "\t\t.section\t.ctors\n";
        } else if (target.equals("macos")) {
            s += "\t\t.mod_init_func";
        } else {
            throw new RuntimeException("Target " + target + " not supported");

        }
        s += "\t\t.align 4\n";
        for (String functionName : compUnit.ctors()) {
            s += "\t\t.quad\t" + functionName + "\n";
        }
        return s;
    }
}
