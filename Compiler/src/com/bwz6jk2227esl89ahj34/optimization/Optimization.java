package com.bwz6jk2227esl89ahj34.optimization;

import com.bwz6jk2227esl89ahj34.Main;
import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.register_allocation.GraphColorer;
import com.bwz6jk2227esl89ahj34.analysis.*;
import com.bwz6jk2227esl89ahj34.analysis.available_copies.AvailableCopiesAnalysis;
import com.bwz6jk2227esl89ahj34.analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions
        .AvailableExpressionSet.TaggedExpression;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions.AvailableExpressionAnalysis;
import com.bwz6jk2227esl89ahj34.analysis.available_expressions.AvailableExpressionAnalysis.ExpressionNodePair;
import com.bwz6jk2227esl89ahj34.analysis.conditional_constants.ConditionalConstantAnalysis;
import com.bwz6jk2227esl89ahj34.analysis
        .conditional_constants.UnreachableValueTuplesPair;
import com.bwz6jk2227esl89ahj34.analysis.live_variables.LiveVariableAnalysis;
import com.bwz6jk2227esl89ahj34.analysis.live_variables.LiveVariableSet;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.visit.AvailableCopiesVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.CommonSubexpressionVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.ConditionalConstantPropagationVisitor;

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
        AvailableExpressionAnalysis analysis = new AvailableExpressionAnalysis(body);

        // Create new set of cse temps
        Map<IRExpr, IRTemp> tempMap = new LinkedHashMap<>();
        for (IRExpr expr : analysis.allExprs) {
            tempMap.put(expr, new IRTemp("cse" + tempCounter++));
        }

        List<IRStmt> stmts = body.stmts();
        Map<Integer, CFGNode> nodes = analysis.getGraph().getNodes();
        Map<ExpressionNodePair, Integer> counts = new LinkedHashMap<>();
        Map<CFGNode, List<IRExpr>> nodeExpressions = new LinkedHashMap<>();

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

        // Find redundant subexpressions that surpass a threshold
        Set<ExpressionNodePair> redundantSubexpressions = new LinkedHashSet<>();
        for (ExpressionNodePair pair : counts.keySet()) {
            int threshold = 1;
            Integer count = counts.get(pair);
            if (count != null && count >= threshold) {
                redundantSubexpressions.add(pair);
            }
        }

        // Replace common subexpressions with their corresponding temp
        Set<ExpressionNodePair> usedTemps = new LinkedHashSet<>();
        for (int i = 0; i < stmts.size(); i++) {
            IRStmt stmt = stmts.get(i);
            for (CFGNode node : nodes.values()) {
                if (!stmt.equals(((CFGNodeIR) node).getStatement())) {
                    continue;
                }
                CommonSubexpressionVisitor visitor =
                        new CommonSubexpressionVisitor((AvailableExpressionSet) node.getOut(), analysis, tempMap, redundantSubexpressions);
                // The visitor keeps track of all the temps it actually used
                IRStmt newStmt = (IRStmt) visitor.visit(stmts.get(i));
                usedTemps.addAll(visitor.usedTemps);
                ((CFGNodeIR) node).setStatement(newStmt);
                stmts.set(i, newStmt);
            }
        }


        for (CFGNode node : nodes.values()) {
            List<IRExpr> exprs = nodeExpressions.get(node);
            // Determine which subexpressions are evaluated enough times to be worth
            // creating a new temp
            Set<IRExpr> neededExprs = new LinkedHashSet<>();
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
                        IRTemp cseTemp = tempMap.get(expr);
                        ExpressionNodePair pair = new ExpressionNodePair(cseTemp, node);
                        if (usedTemps.contains(pair)) {
                            // If this temp is used, add an assignment for it
                            it.previous();
                            it.add(new IRMove(cseTemp, expr));
                            it.next();
                        }
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
        AvailableCopiesAnalysis availableCopiesAnalysis = new AvailableCopiesAnalysis(body);

        Map<Integer, CFGNode> nodes = availableCopiesAnalysis.getGraph().getNodes();
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
    public static IRSeq conditionalConstantPropagation(IRSeq seq) {
        ConditionalConstantAnalysis ccp =
                new ConditionalConstantAnalysis(seq);

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
        boolean fixpointReached = false;
        // Iterate dead code removal until a fixpoint is reached
        while (!fixpointReached) {
            fixpointReached = true;
            // Perform live variable analysis for dead code elimination
            LiveVariableAnalysis analysis = new LiveVariableAnalysis(lines);

            // Remove assignments to variables that aren't used (dead code)
            for (CFGNode node : analysis.getGraph().getNodes().values()) {
                CFGNodeAssembly assemblyNode = (CFGNodeAssembly) node;
                AssemblyInstruction instruction = assemblyNode.getInstruction();
                Set<AssemblyAbstractRegister> outSet = ((LiveVariableSet) node.getOut()).getLiveVars();
                // We only consider instructions that could define a temp that is unused until its
                // next definition
                if (!LiveVariableAnalysis.defOpCodes.contains(instruction.opCode)) {
                    continue;
                }
                assert instruction.getArgs().size() == 2;
                if (instruction.getArgs().get(1) instanceof AssemblyAbstractRegister) {
                    AssemblyAbstractRegister dst = (AssemblyAbstractRegister)
                            instruction.getArgs().get(1);
                    if (outSet.contains(dst)) {
                        // If the outset contains this temp, we know it's needed
                        continue;
                    }
                    // If the outset does not contain the variable we just
                    // define, we know this
                    // code is useless
                    for (Iterator<AssemblyLine> it = lines.iterator(); it
                            .hasNext(); ) {
                        // Find the line in the assembly and remove it
                        AssemblyLine line = it.next();
                        if (line == instruction) {
                            it.remove();
                            fixpointReached = false;
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
        // Remove all instructions of the form: move x to x
        for (Iterator<AssemblyLine> it = lines.iterator(); it.hasNext();) {
            // Find the line in the assembly and remove it
            AssemblyLine line = it.next();
            if (!(line instanceof AssemblyInstruction)) {
                // Only consider instructions
                continue;
            }
            AssemblyInstruction instruction = (AssemblyInstruction) line;
            if (!LiveVariableAnalysis.defOpCodes.contains(instruction.opCode)) {
                // Only consider instructions that could define a temp
                continue;
            }
            if (instruction.getArgs().size() < 2) {
                // We only consider useless moves (move x to x)
                continue;
            }
            AssemblyExpression src = instruction.args.get(0);
            AssemblyExpression dst = instruction.args.get(1);
            if (src.equals(dst)) {
                it.remove();
            }
        }
    }

    public static Map<AssemblyAbstractRegister, AssemblyExpression> allocateRegisters(List<AssemblyLine> lines) {
        // Rerunning live variable analysis after dead code elimination
        LiveVariableAnalysis liveVariableAnalysis = new LiveVariableAnalysis(lines);

        // Constructing the interference sets for register allocation
        List<Set<AssemblyAbstractRegister>> interferenceSets = new LinkedList<>();
        for (CFGNode node : liveVariableAnalysis.getGraph().getNodes().values()) {
            LiveVariableSet out = (LiveVariableSet) node.getOut();
            interferenceSets.add(out.getLiveVars());
        }

        Map<AssemblyAbstractRegister, AssemblyExpression> registerMap = new LinkedHashMap<>();
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
}
