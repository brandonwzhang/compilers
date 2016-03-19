package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr {
    private String name;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    /**
     * Copy constructor (shallow).
     * @param temp
     */
    public IRTemp(IRTemp temp) {
        this.name = temp.name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
        p.printAtom(name);
        p.endList();
    }
}
