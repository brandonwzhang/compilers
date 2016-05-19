package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.visit.NodeVisitor;

/**
 * Created by jihunkim on 5/12/16.
 */
public class Break extends Statement {
    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
