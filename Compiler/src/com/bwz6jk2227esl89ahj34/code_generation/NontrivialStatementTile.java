package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.ir.IRBinOp;
import com.bwz6jk2227esl89ahj34.ir.IRMove;
import com.bwz6jk2227esl89ahj34.ir.IRNode;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;

import java.util.List;

//TODO: rename to something better later
public class NontrivialStatementTile extends StatementTile {
    List<String> tempLabels;
    public NontrivialStatementTile(IRNode pattern, CodeGenerator codeGenerator) {
        super(pattern, codeGenerator);
    }

    @Override
    public boolean match(IRNode root) {
        // Same structure, just have to check temp labels
        if(super.matchHelper(root, pattern)) {
            IRMove castedRoot = (IRMove) root;
            String tempLabel = ((IRTemp)castedRoot.target()).label();
            IRBinOp castedExpr = (IRBinOp)castedRoot.expr();
            String tempLabel2 = castedExpr.left() instanceof IRTemp
                    ? ((IRTemp) castedExpr.left()).label()
                    : ((IRTemp) castedExpr.right()).label();
            return tempLabel.equals(tempLabel2);
        } else {
            return false;
        }
    }

}
