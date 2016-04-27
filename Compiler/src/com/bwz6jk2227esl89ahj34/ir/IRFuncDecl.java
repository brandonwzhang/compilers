package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopies;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionSet;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_expressions
        .AvailableExpressionsAnalysis;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation.ConditionalConstantPropagation;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation.UnreachableValueTuplesPair;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation.Value;
import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.InsnMapsBuilder;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRLowerVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.*;
import com.bwz6jk2227esl89ahj34.util.Util;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;

import java.util.*;

/** An IR function declaration */
public class IRFuncDecl extends IRNode {
    private String name;
    private IRStmt body;

    public IRFuncDecl(String name, IRStmt stmt) {
        this.name = name;
        body = stmt;
    }

    /**
     * Copy constructor (shallow).
     *
     * @param funcdecl
     */
    public IRFuncDecl(IRFuncDecl funcdecl) {
        this.name = funcdecl.name;
        this.body = funcdecl.body;
    }

    public String name() {
        return name;
    }

    public IRStmt body() {
        return body;
    }

    @Override
    public String label() {
        return "FUNC " + name;
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, body);

        if (stmt != body) return new IRFuncDecl(name, stmt);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(body));
        return result;
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        v.addInsn(this);
        return v;
    }

    @Override
    public IRNode buildInsnMaps(InsnMapsBuilder v) {
        return this;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("FUNC");
        p.printAtom(name);
        body.printSExp(p);
        p.endList();
    }

    public int indexOfLabel(String labelName, List<List<IRStmt>> blocks) {
        for (int i = 0; i < blocks.size(); i++) {
            List<IRStmt> block = blocks.get(i);
            if (block.get(0) instanceof IRLabel &&
                    ((IRLabel) (block.get(0))).name().equals(labelName)) {
                return i;
            }
        }
        throw new RuntimeException("Jump to non-existent label: " + labelName);
    }

    public List<Integer> getSuccessors(int i, List<List<IRStmt>> blocks) {
        List<IRStmt> block = blocks.get(i);
        assert block.size() > 0;
        IRStmt lastStatement = block.get(block.size() - 1);
        if (lastStatement instanceof IRCJump) {
            IRCJump cjump = (IRCJump) lastStatement;
            // First add false block, then true block
            List<Integer> successors = new LinkedList<>();
            if (cjump.falseLabel() != null) {
                int falseIndex = indexOfLabel(cjump.falseLabel(), blocks);
                successors.add(falseIndex);
            }
            int trueIndex = indexOfLabel(cjump.trueLabel(), blocks);
            successors.add(trueIndex);
            return successors;
        } else if (lastStatement instanceof IRJump) {
            IRJump jump = (IRJump) lastStatement;
            List<Integer> successors = new LinkedList<>();
            assert jump.target() instanceof IRName;
            String labelName = ((IRName) jump.target()).name();
            int index = indexOfLabel(labelName, blocks);
            successors.add(index);
            return successors;
        } else if (lastStatement instanceof IRReturn) {
            return new LinkedList<>();
        } else {
            List<Integer> successors = new LinkedList<>();
            if (i < blocks.size() - 1) {
                successors.add(i + 1);
            }
            return successors;
        }

    }

    public Map<Integer, List<Integer>> constructFlowGraph(List<List<IRStmt>> blocks) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            graph.put(i, getSuccessors(i, blocks));
        }
        return graph;
    }

    private List<IRStmt> reorderBlocksHelper(int curNode, Map<Integer, List<Integer>> graph,
                                             List<List<IRStmt>> blocks, boolean[] visited) {
        visited[curNode] = true;
        List<IRStmt> block = blocks.get(curNode);
        List<Integer> successors = graph.get(curNode);
        if (successors.size() == 0) {
            return block;
        }
        if (successors.size() == 1) {
            int successor = successors.get(0);
            if (visited[successor]) {
                return block;
            }
            List<IRStmt> retBlock = new LinkedList<>(block);
            retBlock.addAll(reorderBlocksHelper(successors.get(0), graph, blocks, visited));
            return retBlock;
        }

        IRStmt lastStatement = block.get(block.size() - 1);
        // We have a CJump to handle
        assert lastStatement instanceof IRCJump;
        // Lower the CJump
        IRCJump mirCJump = (IRCJump) lastStatement;
        IRCJump canonicalCJump = new IRCJump(mirCJump.expr(), mirCJump.trueLabel());
        block.remove(block.size() - 1);
        block.add(canonicalCJump);

        assert successors.size() == 2;
        int falseNode = successors.get(0);
        int trueNode = successors.get(1);
        if (visited[falseNode] && visited[trueNode]) {
            return block;
        } else if (visited[falseNode]) {
            // Only add true statements
            List<IRStmt> retBlock = new LinkedList<>(block);
            retBlock.addAll(reorderBlocksHelper(trueNode, graph, blocks, visited));
            return retBlock;
        } else if (visited[trueNode]) {
            // Only add false statements
            List<IRStmt> retBlock = new LinkedList<>(block);
            retBlock.addAll(reorderBlocksHelper(falseNode, graph, blocks, visited));
            return retBlock;
        }
        List<IRStmt> falseBlock = reorderBlocksHelper(falseNode, graph, blocks, visited);
        List<IRStmt> trueBlock = reorderBlocksHelper(trueNode, graph, blocks, visited);
        IRStmt lastFalseStatement = falseBlock.get(falseBlock.size() - 1);
        // If last statement in false block doesn't jump or break, we need to add a jump in
        if (!(lastFalseStatement instanceof IRJump || lastFalseStatement instanceof IRReturn)) {
            String exitLabelName = MIRLowerVisitor.getFreshVariable();
            falseBlock.add(new IRJump(new IRName(exitLabelName)));
            trueBlock.add(new IRLabel(exitLabelName));
        }
        List<IRStmt> retBlock = new LinkedList<>(block);
        retBlock.addAll(falseBlock);
        retBlock.addAll(trueBlock);
        return retBlock;
    }

    public List<IRStmt> reorderBlocks(List<IRStmt> stmts) {
        // Holds the basic blocks of the program
        // Split up by "leader instructions" (jump target, after jump, first
        // instruction in the program)
        List<List<IRStmt>> blocks = new LinkedList<>();
        List<IRStmt> temp = new LinkedList<>();
        for (IRStmt stmt : stmts) {
            if (stmt instanceof IRLabel) {
                // We encountered beginning of new block
                blocks.add(new LinkedList<>(temp));
                temp = new LinkedList<>();
            }
            temp.add(stmt);
            if (stmt instanceof IRCJump || stmt instanceof IRJump ||
                    stmt instanceof IRReturn) {
                // We encountered end of block
                blocks.add(new LinkedList<>(temp));
                temp = new LinkedList<>();
            }
        }
        blocks.add(new LinkedList<>(temp));

        // First, clear out all of the empty blocks (in case a label directly
        // follows some sort of jump)
        Iterator<List<IRStmt>> it = blocks.iterator();
        while (it.hasNext()) {
            List<IRStmt> block = it.next();
            if (block.size() == 0) {
                it.remove();
            }
        }
        // If the list of blocks is empty, we don't need to reorder it
        if (blocks.size() == 0) {
            return stmts;
        }

        List<IRStmt> finalStatements = new LinkedList<>();
        finalStatements.addAll(reorderBlocksHelper(0, constructFlowGraph(blocks),
                blocks, new boolean[blocks.size()]));

        // Remove last return statement before we add epilogue block
        if (finalStatements.get(finalStatements.size() - 1) instanceof IRReturn) {
            finalStatements.remove(finalStatements.size() - 1);
        }

        // Add epilogue block
        String epilogueLabelName = MIRLowerVisitor.getFreshVariable();
        finalStatements.add(new IRLabel(epilogueLabelName));
        finalStatements.add(new IRReturn());

        // Replace all instances of return with jump to this label
        for (int i = 0; i < finalStatements.size() - 1; i++) {
            if (finalStatements.get(i) instanceof IRReturn) {
                finalStatements.set(i, new IRJump(new IRName(epilogueLabelName)));
            }
        }

        return finalStatements;
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRFuncDecl;
        // Result of lowering all the children
        // All IRSeq's should be flattened by this point
        IRFuncDecl fd = (IRFuncDecl) n_;
        List<IRStmt> stmts = ((IRSeq) (fd.body())).stmts();
        // jihun: running with CCP for now
        // comment the next 3 lines out and uncomment the return statement
        // to get rid of CCP
        IRSeq reorderedBody = new IRSeq(reorderBlocks(stmts));
        IRSeq ccp_optimized = condtionalConstantPropagation(new IRSeq(reorderedBody));
        // Iterate constant propagation and common subexpression elimination
        for (int i = 0; i < 1; i++) {
            propagateCopies(ccp_optimized);
            eliminateCommonSubexpressions(ccp_optimized);
        }
        return new IRFuncDecl(fd.name(), ccp_optimized);
    }

    /**
     * Runs an available expressions analysis and replaces all common subexpressions
     */
    private void eliminateCommonSubexpressions(IRSeq body) {
        AvailableExpressionsAnalysis analysis = new AvailableExpressionsAnalysis(body);
        Util.writeHelper(
                "subexpressions" + name,
                "dot",
                "./",
                Collections.singletonList(analysis.toString())
        );

        // create new set of cse temps
        int tempCounter = 0;
        Map<IRExpr, IRTemp> tempMap = new HashMap<>();
        for (IRExpr expr : analysis.allExprs) {
            tempMap.put(expr, new IRTemp("cse" + tempCounter++));
        }

        List<IRStmt> stmts = body.stmts();
        Map<Integer, CFGNode> nodes = analysis.getGraph().getNodes();

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
    private void propagateCopies(IRSeq body) {
        AvailableCopies availableCopies = new AvailableCopies(body);
        Util.writeHelper(
                "available" + name,
                "dot",
                "./",
                Collections.singletonList(availableCopies.toString())
        );
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
    public IRSeq condtionalConstantPropagation(IRSeq seq) {
        ConditionalConstantPropagation ccp =
                new ConditionalConstantPropagation(seq);


        Map<Integer, CFGNode> graph = ccp.getGraph().getNodes();
        List<IRStmt> stmts = seq.stmts();
        List<IRStmt> newStmts = new LinkedList<>();
        for (int i = 0; i < stmts.size(); i++) {
            if (!graph.containsKey(i)) {
              newStmts.add(stmts.get(i));
            } else {
                IRStmt stmt = stmts.get(i);
                CFGNode node = graph.get(i);
                UnreachableValueTuplesPair tuple = (UnreachableValueTuplesPair) node.getIn();
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
}
