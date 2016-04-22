package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CheckCanonicalIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRStmt> stmts;

    /**
     * @param stmts the statements
     */
    public IRSeq(IRStmt... stmts) {
        this(Arrays.asList(stmts));
    }

    /**
     * Create a SEQ from a list of statements. The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRStmt> stmts) {
        this.stmts = stmts;
    }

    /**
     * Copy constructor (shallow).
     * @param seq
     */
    public IRSeq(IRSeq seq) {
        this.stmts = seq.stmts;
    }

    public List<IRStmt> stmts() {
        return stmts;
    }

    @Override
    public String label() {
        return "SEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRStmt> results = new ArrayList<>(stmts.size());
        for (IRStmt stmt : stmts) {
            IRStmt newStmt = (IRStmt) v.visit(this, stmt);
            if (newStmt != stmt) modified = true;
            results.add(newStmt);
        }

        if (modified) return new IRSeq(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRStmt stmt : stmts)
            result = v.bind(result, v.visit(stmt));
        return result;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterSeq();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inSeq();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startUnifiedList();
        p.printAtom("SEQ");
        for (IRStmt stmt : stmts)
            stmt.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRSeq;
        List<IRStmt> flattenedResult = new LinkedList<>();
        for (IRStmt r : ((IRSeq)n_).stmts()) {
            addStatements(flattenedResult, r);
        }
        return new IRSeq(flattenedResult);
    }
}
