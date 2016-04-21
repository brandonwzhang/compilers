package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.code_generation.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.OpCode;

import java.util.*;

public class ControlFlowGraphAssembly extends ControlFlowGraph {
    private static int indexOfLabel(String labelName, List<AssemblyLine> lines) {
        for (int i = 0; i < lines.size(); i++) {
            AssemblyLine instruction = lines.get(i);
            if (instruction instanceof AssemblyLabel) {
                if (((AssemblyLabel) instruction).getName().equals(labelName)) {
                    int next = i + 1;
                    // Only return index of the instruction this label points to
                    while (!(lines.get(next) instanceof AssemblyInstruction)) {
                        next++;
                    }
                    return next;
                }
            }
        }
        return -1;
    }

    private static List<Integer> getSuccessors(int i, List<AssemblyLine> lines) {
        List<Integer> successors = new LinkedList<>();
        // We only construct this CFG for lines
        AssemblyInstruction instruction = (AssemblyInstruction) lines.get(i);
        if (instruction.getOpCode() == OpCode.JE || instruction.getOpCode() == OpCode.JNE) {
            // We could fall through the false block
            if (i < lines.size() - 1)  {
                int next = i + 1;
                // Only include instructions in the CFG
                while (!(lines.get(next) instanceof AssemblyInstruction)) {
                    next++;
                }
                successors.add(next);
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
        }
        else if (instruction.getOpCode() == OpCode.RETQ){
            return successors;
        } else {
            // Add the next instruction as a successor unless we are at the last
            // instruction
            if (i < lines.size() - 1) {
                int next = i + 1;
                // Only include instructions in the CFG
                while (!(lines.get(next) instanceof AssemblyInstruction)) {
                    next++;
                }
                successors.add(next);
            }
            return successors;
        }

    }

    public ControlFlowGraphAssembly(List<AssemblyLine> lines) {
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
