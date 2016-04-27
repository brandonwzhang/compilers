package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.CFGNodeIR;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.DataflowAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.LatticeElement;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopies;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionsAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis
        .conditional_constant_propagation.ConditionalConstantPropagation;
import com.bwz6jk2227esl89ahj34.dataflow_analysis
        .conditional_constant_propagation.UnreachableValueTuplesPair;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.visit.AvailableCopiesVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CommonSubexpressionVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.ConditionalConstantPropagationVisitor;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.util.*;

public class Optimization {
    public static String functionName;

    /**
     * Runs an available expressions analysis and replaces all common subexpressions
     */
    public static void eliminateCommonSubexpressions(IRSeq body) {
        AvailableExpressionsAnalysis analysis = new AvailableExpressionsAnalysis(body);
        writeCFG(analysis, "cse");

        // Create new set of cse temps
        int tempCounter = 0;
        Map<IRExpr, IRTemp> tempMap = new HashMap<>();
        for (IRExpr expr : analysis.allExprs) {
            tempMap.put(expr, new IRTemp("cse" + tempCounter++));
        }

        List<IRStmt> stmts = body.stmts();
        Map<Integer, CFGNode> nodes = analysis.getGraph().getNodes();

        // Replace common subexpressions with their corresponding temp
        for (int i : nodes.keySet()) {
            CFGNodeIR node = (CFGNodeIR) nodes.get(i);
            CommonSubexpressionVisitor visitor =
                    new CommonSubexpressionVisitor((AvailableExpressionSet) node.getOut(), analysis, tempMap);
            IRStmt newStmt = (IRStmt) visitor.visit(stmts.get(i));
            node.setStatement(newStmt);
            stmts.set(i, newStmt);
        }

        for (CFGNode node : analysis.getGraph().getNodes().values()) {
            // Find all the expressions that this node adds to the out
            Set<IRExpr> inSet = ((AvailableExpressionSet) node.getIn()).getExprs();
            Set<IRExpr> outSet = ((AvailableExpressionSet) node.getOut()).getExprs();
            Set<IRExpr> exprs = new HashSet<>(outSet);
            exprs.removeAll(inSet);

            for (ListIterator<IRStmt> it = stmts.listIterator(); it.hasNext();) {
                IRStmt stmt = it.next();
                if (stmt == ((CFGNodeIR) node).getStatement()) {
                    // Assign the new temp for the expression
                    for (IRExpr expr : exprs) {
                        it.previous();
                        it.add(new IRMove(tempMap.get(expr), expr));
                        it.next();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Runs an available copies analysis and replaces all copies in body with
     * their mappings.
     */
    public static void propagateCopies(IRSeq body) {
        AvailableCopies availableCopies = new AvailableCopies(body);
        writeCFG(availableCopies, "copy");

        Map<Integer, CFGNode> nodes = availableCopies.getGraph().getNodes();
        List<IRStmt> stmts = body.stmts();
        for (int i = 0; i < stmts.size(); i++) {
            // Get the CFGNode corresponding to the current statement
            CFGNode node = nodes.get(i);
            if (node == null) {
                // If we don't have an instance of a statement, then it's not
                // going to be in the nodes map
                continue;
            }
            LatticeElement in = node.getIn();
            assert in instanceof AvailableCopiesSet;
            // Retrieve available copies at current node
            AvailableCopiesVisitor availableCopiesVisitor =
                    new AvailableCopiesVisitor((AvailableCopiesSet) in);
            // Replace the current statement with one with all copies replaced
            stmts.set(i, (IRStmt) availableCopiesVisitor.visit(stmts.get(i)));
        }

        // Get rid of redundant moves
        for (Iterator<IRStmt> it = stmts.iterator(); it.hasNext();) {
            IRStmt stmt = it.next();
            if (stmt instanceof IRMove) {
                IRMove move = (IRMove) stmt;
                if (move.target() instanceof IRTemp && move.expr() instanceof IRTemp) {
                    IRTemp target = (IRTemp) move.target();
                    IRTemp expr = (IRTemp) move.expr();
                    if (target.name().equals(expr.name())) {
                        it.remove();
                    }
                }
            }
        }
    }

    // NOTE: at the moment, this only eliminates unreachable code
    public static IRSeq condtionalConstantPropagation(IRSeq seq) {
        ConditionalConstantPropagation ccp =
                new ConditionalConstantPropagation(seq);
        writeCFG(ccp, "ccp");

        Map<Integer, CFGNode> graph = ccp.getGraph().getNodes();
        List<IRStmt> stmts = seq.stmts();
        List<IRStmt> newStmts = new LinkedList<>();
        for (int i = 0; i < stmts.size(); i++) {
            if (!graph.containsKey(i)) {
                newStmts.add(stmts.get(i));
            } else {
                IRStmt stmt = stmts.get(i);
                CFGNode node = graph.get(i);
                // out has all the information associated with the node
                UnreachableValueTuplesPair tuple = (UnreachableValueTuplesPair) node.getOut();
                if (tuple.isUnreachable()) {
                    // unreachable so we do not add it to new stmts
                } else {
                    ConditionalConstantPropagationVisitor visitor =
                            new ConditionalConstantPropagationVisitor(tuple.getValueTuples());
                    newStmts.add((IRStmt) visitor.visit(stmt));
                    //newStmts.add(stmt);
                }
            }
        }

        return new IRSeq(newStmts);
    }

    private static void writeCFG(DataflowAnalysis analysis, String optName) {
        Util.writeHelper(
                optName + functionName,
                "dot",
                "./",
                Collections.singletonList(analysis.toString())
        );
    }
}
