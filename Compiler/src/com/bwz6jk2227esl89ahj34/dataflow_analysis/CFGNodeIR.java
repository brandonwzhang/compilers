package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CFGNodeIR extends CFGNode {
    private IRStmt statement;

    public CFGNodeIR(IRStmt statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        String label = "" + statement + "\\n";
        return label + super.toString();
    }
}
