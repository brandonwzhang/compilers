package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies.AvailableCopiesSet.TempPair;

import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.*;

public class AvailableCopies extends DataflowAnalysis {

    public AvailableCopies(IRSeq seq) {
        super(seq, Direction.FORWARD, new AvailableCopiesSet(new HashSet<>()));
    }

    public void transfer(CFGNode node) {
        // perform the analysis on the IR level
        assert node instanceof CFGNodeIR;

        // assumption: out of predecessor is filled in
        List<CFGNode> predecessors = node.getPredecessors();
        Set<LatticeElement> preds = new HashSet<>();
        for (CFGNode predNode : predecessors) {
            preds.add(predNode.getOut());
        }
        node.setIn(meet(preds)); // set in of node to in
        node.setOut(out(node));  // set out of node to result of out method
    }

    // meet is an intersection for AvailableCopies
    // precondition: elements are out of the predecessors
    // postcondition: in[n] = for all predecessors of n n',
    //                        return intersection of out[n']
    public AvailableCopiesSet meet(Set<LatticeElement> elements) {
        if (elements.size() == 0) {
            return new AvailableCopiesSet(new HashSet<>());
        }

        AvailableCopiesSet accumulator = null;
        for(LatticeElement element : elements) {
            if (accumulator == null) {
                Set<TempPair> copies = new HashSet<>(((AvailableCopiesSet) element).getCopies());
                accumulator = new AvailableCopiesSet(copies);
            } else {
                AvailableCopiesSet castedPred = (AvailableCopiesSet) element;
                accumulator.intersect(castedPred);
            }
        }
        return accumulator;
    }

    // out[n] = (in[n] - kill[n]) union gen[n]
    public AvailableCopiesSet out(CFGNode node) {
        return (((AvailableCopiesSet) node.getIn()).subtract(kill(node)))
                .union(gen(node));
    }

    public AvailableCopiesSet gen(CFGNode node) {
        assert node instanceof CFGNodeIR;
        CFGNodeIR castedNode = (CFGNodeIR) node;
        Set<TempPair> genSet = new HashSet<>();
        IRStmt castedNodeStmt = castedNode.getStatement();
        if (castedNodeStmt instanceof IRMove) {
            IRMove move = (IRMove) castedNodeStmt;
            if (move.target() instanceof IRTemp && move.expr() instanceof IRTemp) {
                IRTemp target = (IRTemp) move.target();
                IRTemp expr = (IRTemp) move.expr();
                if (!target.name().equals(expr.name())) {
                    genSet.add(new TempPair(target.name(), expr.name()));
                }
            }
        }
        return new AvailableCopiesSet(genSet);
    }

    public AvailableCopiesSet kill(CFGNode node) {
        assert node instanceof CFGNodeIR;
        // first, unwrap the node
        CFGNodeIR castedNode = (CFGNodeIR) node;
        // we get the statement wrapped up in the node
        IRStmt castedNodeStmt = castedNode.getStatement();
        Set<TempPair> inPairs = ((AvailableCopiesSet) node.getIn()).getCopies();

        // the rule for kill is as follows
        // for any x = e, for all z, z = x is killed and x = z is killed
        //
        // if the wrapped statement is a move, we check for this condition
        // the below if statement checks ^ and if the target of the move
        // is a temp

        // in addition, we kill any pair that contains any _RET or _ARG register
        Set<TempPair> killPairs = new HashSet<>();
        if(castedNodeStmt instanceof IRMove
                && ((IRMove)castedNodeStmt).target() instanceof IRTemp ) {
            // if so, we cast the target to a IRTemp
            String destName = ((IRTemp) ((IRMove)castedNodeStmt).target()).name();
            for (TempPair tempPair : inPairs) {
                if (tempPair.left.equals(destName) || tempPair.right.equals(destName)) {
                    killPairs.add(tempPair);
                }
            }
        }
        return new AvailableCopiesSet(killPairs);
    }
}
