package com.bwz6jk2227esl89ahj34.ir;

import java.util.LinkedHashMap;
import java.util.List;

public class DataSegment extends LinkedHashMap<String, List<IRNode>> {
    public DataSegment() {
        super();
    }

    @Override
    public String toString() {
        String s = "";
        s += "\t\t.data\n";
        s += "\t\t.align 8\n";

        for (String name : keySet()) {
            s += "\t\t.globl\t" + name + "\n";
            s += name + ":\n";
            List<IRNode> values = get(name);
            for (IRNode value : values) {
                assert value instanceof IRConst || value instanceof IRName;
                s += "\t\t.quad\t";
                if (value instanceof IRConst) {
                    s += ((IRConst) value).value();
                } else {
                    s += ((IRName) value).name();
                }
                s += "\n";
            }
        }
        s += "\n";
        return s;
    }
}
