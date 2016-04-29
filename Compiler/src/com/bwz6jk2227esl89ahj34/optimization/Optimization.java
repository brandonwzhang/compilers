package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.register_allocation.GraphColorer;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopies;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet.TaggedExpression;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionsAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionsAnalysis.ExpressionNodePair;
import com.bwz6jk2227esl89ahj34.dataflow_analysis
        .conditional_constant_propagation.ConditionalConstantPropagation;
import com.bwz6jk2227esl89ahj34.dataflow_analysis
        .conditional_constant_propagation.UnreachableValueTuplesPair;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables.LiveVariableAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables.LiveVariableSet;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.visit.AvailableCopiesVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CommonSubexpressionVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.ConditionalConstantPropagationVisitor;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.util.*;

public class Optimization {
    public static String functionName;
    // We use a static counter to avoid colliding namespaces when we iterate
    // CSE multiple times
    private static long tempCounter = 0;

    /**
     * Runs an available expressions analysis and replaces all common subexpressions
     */
    public static void eliminateCommonSubexpressions(IRSeq body) {
        AvailableExpressionsAnalysis analysis = new AvailableExpressionsAnalysis(body);
        writeCFG(analysis, "cse");

        // Create new set of cse temps
        Map<IRExpr, IRTemp> tempMap = new HashMap<>();
        for (IRExpr expr : analysis.allExprs) {
            tempMap.put(expr, new IRTemp("cse" + tempCounter++));
        }

        List<IRStmt> stmts = body.stmts();
        Map<Integer, CFGNode> nodes = analysis.getGraph().getNodes();
        Map<ExpressionNodePair, Integer> counts = new HashMap<>();
        Map<CFGNode, List<IRExpr>> nodeExpressions = new HashMap<>();

        for (CFGNode node : nodes.values()) {
            // Get the set of expressions evaluated at this node
            AvailableExpressionSet set = analysis.eval(node);
            Set<TaggedExpression> taggedExprs = set.getExprs();
            // Get the set of expressions available at this node
            AvailableExpressionSet inSet = (AvailableExpressionSet) node.getIn();
            Set<TaggedExpression> inTaggedExprs = inSet.getExprs();
            // Make a new list of exprs for this node
            nodeExpressions.put(node, new LinkedList<>());
            for (TaggedExpression taggedExpr : taggedExprs) {
                nodeExpressions.get(node).add(taggedExpr.expr);
                // If we're redundantly evaluating an expression, increment the count
                if (inTaggedExprs.contains(taggedExpr)) {
                    ExpressionNodePair pair = new ExpressionNodePair(taggedExpr);
                    Integer count = counts.get(pair);
                    if (count == null) {
                        counts.put(pair, 1);
                    } else {
                        counts.put(pair, count + 1);
                    }
                }

            }
        }

        Set<ExpressionNodePair> redundantSubexpressions = new HashSet<>();
        for (ExpressionNodePair pair : counts.keySet()) {
            int threshold = 1;
            Integer count = counts.get(pair);
            if (count != null && count >= threshold) {
                redundantSubexpressions.add(pair);
            }
        }

        for (CFGNode node : nodes.values()) {
            List<IRExpr> exprs = nodeExpressions.get(node);
            // Determine which subexpressions are evaluated enough times to be worth
            // creating a new temp
            Set<IRExpr> neededExprs = new HashSet<>();
            for (IRExpr expr : exprs) {
                ExpressionNodePair pair = new ExpressionNodePair(expr, node);
                if (redundantSubexpressions.contains(pair)) {
                    neededExprs.add(expr);
                }
            }

            for (ListIterator<IRStmt> it = stmts.listIterator(); it.hasNext();) {
                IRStmt stmt = it.next();
                if (stmt == ((CFGNodeIR) node).getStatement()) {
                    // Assign the new temp for the expression
                    for (IRExpr expr : neededExprs) {
                        it.previous();
                        it.add(new IRMove(tempMap.get(expr), expr));
                        it.next();
                    }
                    break;
                }
            }
        }

        // Replace common subexpressions with their corresponding temp
        for (int i = 0; i < stmts.size(); i++) {
            IRStmt stmt = stmts.get(i);
            for (CFGNode node : nodes.values()) {
                if (!stmt.equals(((CFGNodeIR) node).getStatement())) {
                    continue;
                }
                CommonSubexpressionVisitor visitor =
                        new CommonSubexpressionVisitor((AvailableExpressionSet) node.getOut(), analysis, tempMap, redundantSubexpressions);

                IRStmt newStmt = (IRStmt) visitor.visit(stmts.get(i));
                stmts.set(i, newStmt);
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
                    if (Main.optimizationOn(OptimizationType.CP)) {
                        ConditionalConstantPropagationVisitor visitor =
                                new ConditionalConstantPropagationVisitor(tuple.getValueTuples());
                        newStmts.add((IRStmt) visitor.visit(stmt));
                    } else {
                        newStmts.add(stmt);
                    }
                }
            }
        }

        return new IRSeq(newStmts);
    }

    /**
     * Uses live variable analysis to remove assignments to variables
     * that are never used.
     */
    public static void removeDeadCode(List<AssemblyLine> lines) {
        // Perform live variable analysis for dead code elimination
        LiveVariableAnalysis liveVariables = new LiveVariableAnalysis(lines);

        // Remove assignments to variables that aren't used (dead code)
        for (CFGNode node : liveVariables.getGraph().getNodes().values()) {
            CFGNodeAssembly assemblyNode = (CFGNodeAssembly) node;
            AssemblyInstruction instruction = assemblyNode.getInstruction();
            Set<AssemblyAbstractRegister> outSet = ((LiveVariableSet) node.getOut()).getLiveVars();
            if (instruction.opCode == AssemblyInstruction.OpCode.MOVQ) {
                assert instruction.getArgs().size() == 2;
                if (instruction.getArgs().get(1) instanceof AssemblyAbstractRegister) {
                    AssemblyAbstractRegister dst = (AssemblyAbstractRegister) instruction.getArgs().get(1);
                    if (!outSet.contains(dst)) {
                        for (Iterator<AssemblyLine> it = lines.iterator(); it.hasNext();) {
                            AssemblyLine line = it.next();
                            if (line == instruction) {
                                it.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes redundant moves that result from move coalescing.
     */
    public static void removeRedundantMoves(List<AssemblyLine> lines) {
        // Remove duplicate lines
        int i = 0;
        while (i < lines.size()) {
            if (!(lines.get(i) instanceof AssemblyInstruction)) {
                i++;
                continue;
            }
            AssemblyInstruction instruction = (AssemblyInstruction) lines.get(i);
            if (!(instruction.getOpCode() == AssemblyInstruction.OpCode.MOVQ)) {
                i++;
                continue;
            }

            List<AssemblyExpression> args = instruction.args;
            if (args.get(0).equals(args.get(1))){
                lines.remove(i);
            } else {
                i++;
            }
        }
    }

    public static Map<AssemblyAbstractRegister, AssemblyExpression> allocateRegisters(List<AssemblyLine> lines) {
        // Rerunning live variable analysis after dead code elimination
        LiveVariableAnalysis liveVariables = new LiveVariableAnalysis(lines);

        // Constructing the interference sets for register allocation
        List<Set<AssemblyAbstractRegister>> interferenceSets = new LinkedList<>();
        for (CFGNode node : liveVariables.getGraph().getNodes().values()) {
            LiveVariableSet out = (LiveVariableSet) node.getOut();
            interferenceSets.add(out.getLiveVars());
        }

        Map<AssemblyAbstractRegister, AssemblyExpression> registerMap = new HashMap<>();
        if (Main.optimizationOn(OptimizationType.REG)) {
            if (Main.debugOn()) {
                System.out.println("DEBUG: performing optimization: register allocation");
            }
            // Use Kempe's algorithm to allocate physical locations to abstract registers
            GraphColorer graphColorer = new GraphColorer(interferenceSets, lines);
            graphColorer.colorGraph();
            registerMap = graphColorer.getColoring();
        }

        return registerMap;
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
