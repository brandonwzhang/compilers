package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.*;

public class CFG {
    private static int indexOfLabel(String labelName, List<IRStmt> statements) {
        for (int i = 0; i < statements.size(); i++) {
            IRStmt statement = statements.get(i);
            if (statement instanceof IRLabel) {
                if (((IRLabel) statement).name().equals(labelName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static List<Integer> getSuccessors(int i, List<IRStmt> statements) {
        IRStmt statement = statements.get(i);
        if (statement instanceof IRCJump) {
            IRCJump cjump = (IRCJump) statement;
            // First add false block, then true block
            List<Integer> successors = new LinkedList<>();
            if (cjump.falseLabel() != null) {
                int falseIndex = indexOfLabel(cjump.falseLabel(), statements);
                successors.add(falseIndex);
            } else {
                // If we have no false label, we could fall through to the next
                // statement
                if (i < statements.size() - 1)  {
                    successors.add(i + 1);
                }

            }
            int trueIndex = indexOfLabel(cjump.trueLabel(), statements);
            successors.add(trueIndex);
            return successors;
        } else if (statement instanceof IRJump) {
            IRJump jump = (IRJump) statement;
            List<Integer> successors = new LinkedList<>();
            assert jump.target() instanceof IRName;
            String labelName = ((IRName) jump.target()).name();
            int index = indexOfLabel(labelName, statements);
            successors.add(index);
            return successors;
        }
        else if (statement instanceof IRReturn){
            return new LinkedList<>();
        } else {
            // Add the next statement as a successor unless we are at the last
            // statement
            List<Integer> successors = new LinkedList<>();
            if (i < statements.size() - 1) {
                successors.add(i + 1);
            }
            return successors;
        }

    }

    public static Map<Integer, List<Integer>> construct(List<IRStmt> statements) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < statements.size(); i++) {
            graph.put(i, getSuccessors(i, statements));
        }
        return graph;
    }


}
