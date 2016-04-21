package com.bwz6jk2227esl89ahj34.dataflow_analysis;

import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CFGNodeIR extends CFGNode {
    private IRStmt statement;

    public CFGNodeIR(IRStmt statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return statement.toString().substring(0, statement.toString().length() - 1);
    }
}
