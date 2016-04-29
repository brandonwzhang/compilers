package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CFG {
    protected Map<Integer, CFGNode> nodes = new HashMap<>();
    protected Map<Integer, List<Integer>> graph = new HashMap<>();

    @Override
    public String toString() {
        String s = "";
        s += "digraph CFG {\n";
        for (int index : nodes.keySet()) {
            CFGNode node = nodes.get(index);
            s += "\t" + index + " [label=\"" + node + "\"];\n";

            for (int successor : graph.get(index)) {
                s += "\t" + index + " -> " + successor + ";\n";
            }
        }
        s += "}";
        return s;
    }
}
