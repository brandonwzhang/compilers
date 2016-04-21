package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.*;

public class ControlFlowGraphIR extends ControlFlowGraph {
    private static int indexOfLabel(String labelName, List<IRStmt> statements) {
        for (int i = 0; i < statements.size(); i++) {
            IRStmt statement = statements.get(i);
            if (statement instanceof IRLabel) {
                if (((IRLabel) statement).name().equals(labelName)) {
                    int next = i + 1;
                    // Only return index of the instruction this label points to
                    while (statements.get(next) instanceof IRLabel) {
                        next++;
                    }
                    return next;
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
                    int next = i + 1;
                    // Don't include IRLabels in this CFG
                    while (statements.get(next) instanceof IRLabel) {
                        next++;
                    }
                    successors.add(next);
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
                int next = i + 1;
                // Don't include IRLabels in this CFG
                while (statements.get(next) instanceof IRLabel) {
                    next++;
                }
                successors.add(next);
            }
            return successors;
        }

    }

    public ControlFlowGraphIR(List<IRStmt> statements) {
        // First, add all CFG nodes and construct the graph
        for (int i = 0; i < statements.size(); i++) {
            IRStmt statement = statements.get(i);
            // Don't include labels in the CFG
            if (!(statement instanceof IRLabel)) {
                nodes.put(i, new CFGNodeIR(statement));
                graph.put(i, getSuccessors(i, statements));
            }
        }
        // Fill in the predecessors and successors for CFG nodes
        for (int i : nodes.keySet()) {
            CFGNode node = nodes.get(i);
            List<Integer> successors = graph.get(i);
            for (int successor : successors) {
                CFGNode successorNode = nodes.get(successor);
                node.addSuccessor(successorNode);
                successorNode.addPredecessor(node);
            }
        }
    }
}
