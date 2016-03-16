package com.bwz6jk2227esl89ahj34;


import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.visit.*;
import com.bwz6jk2227esl89ahj34.util.SExpPrinter;

public class IRSexyPrintVisitor extends IRVisitor {
    private SExpPrinter printer;

    public IRSexyPrintVisitor(SExpPrinter printer) {
        this.printer = printer;
    }

    @Override
    protected IRVisitor enter(IRNode parent, IRNode n) {
        n.printSExp(this.printer);
        return this; //line should be ignored
    }
}
