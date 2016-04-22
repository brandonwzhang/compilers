package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** RETURN statement */
@Data
@EqualsAndHashCode(callSuper = false)
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
