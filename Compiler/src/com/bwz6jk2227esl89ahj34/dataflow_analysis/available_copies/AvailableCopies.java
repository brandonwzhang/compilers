package com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNodeIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.util.prettyprint.Pair;
import com.bwz6jk2227esl89ahj34.ir.IRStmt;

import java.util.*;

/**
 * Created by jihunkim on 4/20/16.
 */
public class AvailableCopies extends DataflowAnalysis {

     public AvailableCopies(IRSeq seq) {
         super(seq, Direction.FORWARD);
     }

     public void transfer(CFGNode node) {
         // perform the analysis on the IR level
         assert node instanceof CFGNodeIR;

         // unwrap the IR stmt contained by node
         CFGNodeIR castedNode = (CFGNodeIR) node;
         IRStmt stmt = castedNode.getStatement();

         // assumption: out of predecessor is filled in
         AvailableCopiesSet in;
         Set<CFGNode> predecessors = node.getPredecessors();
         if (predecessors.size() > 1) { // if multiple predecessors...
             Set<LatticeElement> preds = new HashSet<>();
             for (CFGNode predNode : predecessors) {
                 preds.add(predNode.getOut());
             }                 // we meet the out of the predecessors
             in = meet(preds); // and set it as the in
         } else { // if single predecessor, set the out as the in
             in = (AvailableCopiesSet) predecessors.iterator().next().getOut();
         }

         node.setIn(in); // set in of node to in
         node.setOut(out(node));  // set out of node to result of out method

     }

     // meet is an intersection for AvailableCopies
     // precondition: elements are out of the predecessors
     // postcondition: in[n] = for all predecessors of n n',
     //                        return intersection of out[n']
     public AvailableCopiesSet meet(Set<LatticeElement> elements) {
         AvailableCopiesSet accumulator = null;
         for(LatticeElement pred : elements) {
             if (accumulator == null) {
                 accumulator = (AvailableCopiesSet) pred;
             } else {
                 AvailableCopiesSet castedPred = (AvailableCopiesSet) pred;
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
         Map<IRTemp, IRTemp> genMap = new HashMap<>();
         IRStmt castedNodeStmt = castedNode.getStatement();
         if (castedNodeStmt instanceof IRMove
                 && ((IRMove)castedNodeStmt).target() instanceof IRTemp
                 && ((IRMove)castedNodeStmt).expr() instanceof IRTemp) {
                 genMap.put((IRTemp)((IRMove)castedNodeStmt).target()
                         , (IRTemp)((IRMove)castedNodeStmt).expr());
         }
         return new AvailableCopiesSet(genMap);
     }

     public AvailableCopiesSet kill(CFGNode node) {
         assert node instanceof CFGNodeIR;
         CFGNodeIR castedNode = (CFGNodeIR) node;
         Map<IRTemp, IRTemp> killMap = new HashMap<>();
         Map<IRTemp, IRTemp> in = ((AvailableCopiesSet)node.getIn()).getMap();

         IRStmt castedNodeStmt = castedNode.getStatement();
         if(castedNodeStmt instanceof IRMove
                 && ((IRMove)castedNodeStmt).target() instanceof IRTemp ) {
             IRTemp dest = (IRTemp) ((IRMove)castedNodeStmt).target();
             for (IRTemp key : in.keySet()) {
                 if (in.get(key).name().equals(dest.name())
                         || key.name().equals(dest.name())) {
                     killMap.put(key, in.get(key));
                 }
             }
         }
         return new AvailableCopiesSet(killMap);
     }
}
