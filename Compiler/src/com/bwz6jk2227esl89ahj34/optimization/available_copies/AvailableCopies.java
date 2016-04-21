package com.bwz6jk2227esl89ahj34.optimization.available_copies;

import com.bwz6jk2227esl89ahj34.ir.IRStmt;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode;
import com.bwz6jk2227esl89ahj34.optimization.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode.NodeType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jihunkim on 4/20/16.
 */
public class AvailableCopies extends DataflowAnalysis {

     public void transfer(CFGNode node) {
         // perform the analysis on the IR level
         assert node.getNodeType() == NodeType.IR;

         // unwrap the IR stmt contained by node
         IRStmt stmt = node.getIrstmt();

         // assumption: out of predecessor is filled in
         AvailableCopiesSet in;
         Set<CFGNode> predecessors = node.getPredecessors();
         if (predecessors.size() > 1) { 
             Set<LatticeElement> preds = new HashSet<>();
             for (CFGNode predNode : predecessors) {
                 preds.add(predNode.getOut());
             }
             in = meet(preds);
         } else {
             in = (AvailableCopiesSet) predecessors.iterator().next().getOut();
         }

         node.setIn(in);
         node.setOut(out(node));

     }

     public AvailableCopiesSet meet(Set<LatticeElement> elements) {
         return null;
     }

     public AvailableCopiesSet out(CFGNode node) {
        return null;
     }

     public AvailableCopiesSet kill(CFGNode node) {
         return null;
     }

     public AvailableCopiesSet gen(CFGNode node) {
        return null;
     }
}
