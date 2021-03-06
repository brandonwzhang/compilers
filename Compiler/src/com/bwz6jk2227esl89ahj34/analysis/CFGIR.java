package com.bwz6jk2227esl89ahj34.analysis;

import com.bwz6jk2227esl89ahj34.ir.*;

import java.util.LinkedList;
import java.util.List;

public class CFGIR extends CFG {
    /**
     * Returns the index of the first non label after index i
     */
    private static int nextNonLabelIndex(int i, List<IRStmt> statements) {
        if (i < statements.size() - 1) {
            int next = i + 1;
            // Don't include IRLabels in this CFG
            while (statements.get(next) instanceof IRLabel) {
                next++;
                if (next >= statements.size()) {
                    return -1;
                }
            }
            return next;
        }
        return -1;
    }

    private static int indexOfLabel(String labelName, List<IRStmt> statements) {
        for (int i = 0; i < statements.size(); i++) {
            IRStmt statement = statements.get(i);
            if (statement instanceof IRLabel) {
                if (((IRLabel) statement).name().equals(labelName)) {
                    int nextNonLabelIndex = nextNonLabelIndex(i, statements);
                    if (nextNonLabelIndex < 0) {
                        throw new RuntimeException("Jump to non-existent label: " + labelName);
                    }
                    return nextNonLabelIndex;

                }
            }
        }
        throw new RuntimeException("Jump to non-existent label: " + labelName);
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
                int nextInstructionIndex = nextNonLabelIndex(i, statements);
                if (nextInstructionIndex >= 0) {
                    successors.add(nextInstructionIndex);
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
        } else if (statement instanceof IRReturn) {
            return new LinkedList<>();
        } else {
            // Add the next statement as a successor unless we are at the last
            // statement
            List<Integer> successors = new LinkedList<>();
            int nextInstructionIndex = nextNonLabelIndex(i, statements);
            if (nextInstructionIndex >= 0) {
                successors.add(nextInstructionIndex);
            }
            return successors;
        }
    }

    public CFGIR(IRSeq seq) {
        List<IRStmt> statements = seq.stmts();
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
