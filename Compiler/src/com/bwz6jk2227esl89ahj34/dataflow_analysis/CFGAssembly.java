package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLabel;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyLine;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyName;

import java.util.LinkedList;
import java.util.List;

public class CFGAssembly extends CFG {
    private static int indexOfLabel(String labelName, List<AssemblyLine> lines) {
        for (int i = 0; i < lines.size(); i++) {
            AssemblyLine line = lines.get(i);
            if (line instanceof AssemblyLabel) {
                if (((AssemblyLabel) line).getName().getName().equals(labelName)) {
                    int next = i + 1;
                    // Only return index of the line this label points to
                    while (!(lines.get(next) instanceof AssemblyInstruction)) {
                        next++;
                    }
                    return next;
                }
            }
        }
        throw new RuntimeException("Jump to non-existent label: " + labelName);
    }

    /**
     * Returns the index of the first instruction after index i
     */
    private static int nextInstructionIndex(int i, List<AssemblyLine> lines) {
        if (i < lines.size() - 1) {
            int next = i + 1;
            // Only include instructions in the CFG
            while (!(lines.get(next) instanceof AssemblyInstruction)) {
                if (next < lines.size()) {
                    return -1;
                }
                next++;
            }
            return next;
        }
        return -1;
    }

    private static List<Integer> getSuccessors(int i, List<AssemblyLine> lines) {
        List<Integer> successors = new LinkedList<>();
        // We only construct this CFG for lines
        AssemblyInstruction instruction = (AssemblyInstruction) lines.get(i);
        if (instruction.getOpCode() == OpCode.JE || instruction.getOpCode() == OpCode.JNE) {
            // We could fall through the false block
            int nextInstructionIndex = nextInstructionIndex(i, lines);
            if (nextInstructionIndex >= 0) {
                successors.add(nextInstructionIndex);
            }

            String labelName = ((AssemblyName)instruction.getArgs().get(0)).getName();
            int trueIndex = indexOfLabel(labelName, lines);
            successors.add(trueIndex);
            return successors;
        } else if (instruction.getOpCode() == OpCode.JMP) {
            assert instruction.getArgs().size() == 1;
            String labelName = ((AssemblyName)instruction.getArgs().get(0)).getName();
            int index = indexOfLabel(labelName, lines);
            successors.add(index);
            return successors;
        } else if (instruction.getOpCode() == OpCode.RETQ) {
            return successors;
        } else {
            // Add the next instruction as a successor unless we are at the last
            // instruction
            int nextInstructionIndex = nextInstructionIndex(i, lines);
            if (nextInstructionIndex >= 0) {
                successors.add(nextInstructionIndex);
            }
            return successors;
        }
    }

    public CFGAssembly(List<AssemblyLine> lines) {
        for (int i = 0; i < lines.size(); i++) {
            AssemblyLine line = lines.get(i);
            if (line instanceof AssemblyInstruction) {
                nodes.put(i, new CFGNodeAssembly((AssemblyInstruction) line));
                graph.put(i, getSuccessors(i, lines));
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
