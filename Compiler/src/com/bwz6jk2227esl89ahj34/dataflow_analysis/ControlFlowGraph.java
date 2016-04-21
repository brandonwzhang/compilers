package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ControlFlowGraph {
    protected Map<Integer, CFGNode> nodes = new HashMap<>();
    protected Map<Integer, List<Integer>> graph = new HashMap<>();

    @Override
    public String toString() {
        String s = "";
        s += "digraph CFG {\n";
        for (int index : nodes.keySet()) {
            CFGNode node = nodes.get(index);
            String label;
            if (node instanceof CFGNodeAssembly) {
                label = "" + ((CFGNodeAssembly) node).getInstruction();
            } else {
                label = "" + ((CFGNodeIR) node).getStatement();
            }
            s += "\t" + index + " [label=\"" + label + "\"];\n";

//            for (CFGNode successor : node.getSuccessors()) {
//                s += node.getIndex() + " -> " + successor.getIndex() + ";\n";
//            }

            for (int successor : graph.get(index)) {
                s += "\t" + index + " -> " + successor + ";\n";
            }
        }
        s += "}";
        return s;
    }
}
