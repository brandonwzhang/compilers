package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.util.SExpPrinter;

/** RETURN statement */
public class IRReturn extends IRStmt {

    @Override
    public String label() {
        return "RETURN";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        p.endList();
    }
}
