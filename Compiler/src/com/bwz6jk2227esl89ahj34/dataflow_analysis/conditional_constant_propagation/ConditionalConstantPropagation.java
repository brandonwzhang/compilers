package com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation;


import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

import java.math.BigInteger;
import java.util.*;

public class ConditionalConstantPropagation extends DataflowAnalysis {

    public ConditionalConstantPropagation(IRSeq seq) {
        super(seq, Direction.FORWARD,
                new UnreachableValueTuplesPair(findAllTemps(seq)), null);
    }

    public static List<IRTemp> findAllTemps(IRSeq seq) {
        List<IRStmt> stmts = seq.stmts();
        Set<IRTemp> temps = new HashSet<>();
        for (IRStmt stmt : stmts) {
            if (stmt instanceof IRMove) {
                IRMove castedStmt = (IRMove) stmt;
                IRExpr target = castedStmt.target();
                if (target instanceof IRTemp) {
                    temps.add((IRTemp) target);
                }
                IRExpr expr = castedStmt.expr();
                if (expr instanceof IRTemp) {
                    temps.add((IRTemp) expr);
                }
            }
        }
        return new LinkedList<>(temps);
    }

    // assumption: ins will always be defined appropriately
    public void transfer(CFGNode node) {
        // we are performing the analysis on the IR level so
        // make sure that we are at the right place

        // unwrap the IR stmt contained by node
        IRStmt stmt = ((CFGNodeIR)node).getStatement();
        System.out.println(stmt);

        // the "in" is filled in as a precondition
        UnreachableValueTuplesPair in =
                (UnreachableValueTuplesPair) node.getIn();

        // root is always reachable
        if (node.getPredecessors().size() == 0) {
            in.setUnreachable(false);
        }

        if (in.isUnreachable()) {
            return;
        }
        // we will update the newMap as we perform the analysis
        // if no updates are made then we are basically passing the map given
        // by the "in"
        Map<String, LatticeElement> newMap = new HashMap<>(in.getValueTuples());

        if (stmt instanceof IRCJump) { // if (...)
            IRCJump castedStmt = (IRCJump) stmt;
            IRExpr guard = castedStmt.expr();
            if (guard instanceof IRTemp) { // if (x)
                IRTemp castedGuard = (IRTemp) guard;
                LatticeElement value = newMap.get(castedGuard.name());
                if (value instanceof Value) { // x = an actual value
                    guard = ((Value) value).getValue();
                    // this will trigger a if statement later in the code
                }
            } else if (guard instanceof IRBinOp) { // if (x binop y)
                IRBinOp castedGuard = (IRBinOp) guard;
                IRExpr left = castedGuard.left();
                IRExpr right = castedGuard.right();

                // can we substitute in const values for left and right?
                if (left instanceof IRTemp) {
                    String leftName = ((IRTemp) left).name();
                    if (newMap.get(leftName) instanceof Value) {
                        left = ((Value) (newMap.get(leftName))).getValue();
                    }
                }
                if (right instanceof IRTemp) {
                    String rightName = ((IRTemp) right).name();
                    if (newMap.get(rightName) instanceof Value) {
                        right = ((Value) (newMap.get(rightName))).getValue();
                    }
                }

                // if we can then simplify the binop!
                if (left instanceof IRConst && right instanceof IRConst) {
                    if (castedGuard.opType() != OpType.DIV ||
                            ((IRConst)right).value() != 0) {
                        guard = computeBinOp(
                                new IRBinOp(castedGuard.opType(), left, right)
                        );
                        // this will trigger the if statement later in the code
                    }
                }
            }

            // if the guard is a const we can make one of the branches
            // "unreachable"
            if (guard instanceof IRConst) {
                IRConst castedGuard = (IRConst) guard;
                UnreachableValueTuplesPair falsePair;
                UnreachableValueTuplesPair truePair;
                assert node.getSuccessors().size() == 2;
                if (castedGuard.value() == 1) { // if (...) always goes to true
                    // then we have to kill the false branch by sending
                    // (true, (T,..,T))
                    // and send down appropriate info to true branch
                    truePair = new UnreachableValueTuplesPair(false, newMap);
                    Map<String, LatticeElement> tops = new HashMap<>();
                    for (String temp : newMap.keySet()) {
                        tops.put(temp, new LatticeTop());
                    }
                    falsePair = new UnreachableValueTuplesPair(true, tops);
                } else { // if (...) always goes to false
                    falsePair = new UnreachableValueTuplesPair(false, newMap);
                    Map<String, LatticeElement> tops = new HashMap<>();
                    for (String temp : newMap.keySet()) {
                        tops.put(temp, new LatticeTop());
                    }
                    truePair = new UnreachableValueTuplesPair(true, tops);
                }
                // now we set the ins of the successor
                // however, we do a null check; if the in is not null, then
                // we meet it with whatever is already present at the in
                int count = 0; // count = 0 -> false branch
                // otherwise true branch
                for (CFGNode successor : node.getSuccessors()) {
                    setIn(successor, count == 0 ? falsePair : truePair);
                    count++;
                }

            } else { // otherwise we can't make any of the branches "unreachable"
                // so we pass down the same information to all the branches
                for (CFGNode successor : node.getSuccessors()) {
                    setIn(successor,
                            new UnreachableValueTuplesPair(in.isUnreachable(),
                                    newMap));
                }
            }
        } else if (stmt instanceof IRMove) { //  ... = ...
            IRMove castedNode = (IRMove) stmt;
            if (castedNode.target() instanceof IRTemp) { // x = ...
                String castedTarget = ((IRTemp) castedNode.target()).name();
                if (castedNode.expr() instanceof IRConst) { // x = const
                    // update the newMap so that x = const
                    Value val = new Value((IRConst) castedNode.expr());
                    newMap.put(castedTarget, valueMeet(newMap.get(castedTarget), val));
                } else if (castedNode.expr() instanceof IRCall) {
                    // a function call can return anything so variable becomes
                    // "overloaded"
                    newMap.put(castedTarget, new LatticeBottom());
                } else if (castedNode.expr() instanceof IRBinOp) {
                    IRExpr left = ((IRBinOp) castedNode.expr()).left();
                    IRExpr right = ((IRBinOp) castedNode.expr()).right();
                    if (left instanceof IRTemp && newMap.containsKey(((IRTemp)left).name())) {
                        LatticeElement leftElement = newMap.get(((IRTemp)left).name());
                        if (leftElement instanceof Value) {
                            left = ((Value)leftElement).getValue();
                        }
                    }
                    if (right instanceof IRTemp && newMap.containsKey(((IRTemp)right).name())) {
                        LatticeElement rightElement = newMap.get(((IRTemp)right).name());
                        if (rightElement instanceof Value) {
                            right = ((Value)rightElement).getValue();
                        }
                    }
                    if (left instanceof IRConst && right instanceof IRConst) {
                        IRConst simplifiedResult = computeBinOp(
                                new IRBinOp(
                                        ((IRBinOp) castedNode.expr()).opType(),
                                        left,
                                        right
                                )
                        );
                        newMap.put(castedTarget, new Value(simplifiedResult));
                    } else {
                        newMap.put(castedTarget, new LatticeBottom());
                    }
                } else if (castedNode.expr() instanceof IRTemp) {
                    // x = y then we update x so that it takes the value of y
                    newMap.put(castedTarget, newMap.get(((IRTemp) castedNode.expr()).name()));
                } else if (castedNode.expr() instanceof IRMem) {
                    // MEM is hard
                    newMap.put(castedTarget, new LatticeBottom());
                } else { // shouldn't reach here
                    System.out.println(castedNode.expr());
                    throw new RuntimeException("Please contact jk2227@cornell.edu");
                }
            }
            for (CFGNode successor : node.getSuccessors()) {
                setIn(successor,
                        new UnreachableValueTuplesPair(in.isUnreachable(),
                                newMap));
            }
        } else {
            for (CFGNode successor : node.getSuccessors()) {
                setIn(successor,
                        new UnreachableValueTuplesPair(in.isUnreachable(),
                                newMap));
            }
        }
    }

    // we set the successor's in as newIn
    public void setIn(CFGNode successor, UnreachableValueTuplesPair newIn) {
        if (successor.getIn() == null) { // if in is null prior, then
            successor.setIn(newIn);  // in = newIn
        } else if (newIn.isUnreachable()) {
            // we don't do anything if new in is unreachable
        } else { // otherwise we "meet" it
            Set<LatticeElement> ins = new HashSet<>(Arrays.asList(successor.getIn(), newIn));
            successor.setIn(meet(ins));
        }
    }

    // meet operator for CCP
    public UnreachableValueTuplesPair meet(Set<LatticeElement> elements) {
        assert elements.size() >= 2;
        Iterator<LatticeElement> iterator = elements.iterator();
        UnreachableValueTuplesPair accumulator = (UnreachableValueTuplesPair) iterator.next();
        while(iterator.hasNext()) {
            UnreachableValueTuplesPair next = (UnreachableValueTuplesPair) iterator.next();
            accumulator.setUnreachable(accumulator.isUnreachable()
                    && next.isUnreachable()); // AND the unreachable boolean values
            Map<String, LatticeElement> accumulatorMap = accumulator.getValueTuples();
            Map<String, LatticeElement> nextMap = next.getValueTuples();
            for (String key : accumulatorMap.keySet()) { // update values accordingly
                accumulatorMap.put(key,
                        valueMeet(accumulatorMap.get(key), nextMap.get(key)));
            }
            accumulator.setValueTuples(accumulatorMap);
        }
        return accumulator;
    }

    // 2 + top = 2 ; 2 + 3 = bottom; 2 + 2 = 2; bottom + anything = bottom
    public LatticeElement valueMeet(LatticeElement e1, LatticeElement e2) {
        if(e1 instanceof LatticeBottom || e2 instanceof LatticeBottom) {
            return new LatticeBottom();
        } else if (e1 instanceof LatticeTop) {
            return e2;
        } else if (e2 instanceof LatticeTop) {
            return e1;
        } else {
            assert e1 instanceof Value && e2 instanceof Value;
            if (!((Value)e1).equals((Value)e2)) {
                return new LatticeBottom();
            } else {
                return e1;
            }
        }
    }

    // precondition: left and right are both IRConst
    // computes binop expression to const
    public static IRConst computeBinOp(IRBinOp bop) {
        BigInteger left = new BigInteger(""+((IRConst)(bop.left())).value());
        BigInteger right = new BigInteger(""+((IRConst)(bop.right())).value());
        switch(bop.opType()) {
            case ADD:
                return new IRConst(left.add(right).longValue());
            case SUB:
                return new IRConst(left.subtract(right).longValue());
            case MUL:
                return new IRConst(left.multiply(right).longValue());
            case HMUL:
                return new IRConst(left.multiply(right).longValue() >> 64);
            case DIV:
                return new IRConst(left.divide(right).longValue());
            case MOD:
                return new IRConst(left.mod(right).longValue());
            case AND:
                return new IRConst(left.and(right).longValue());
            case OR:
                return new IRConst(left.or(right).longValue());
            case XOR:
                return new IRConst(left.xor(right).longValue());
            case LSHIFT:
                return new IRConst(left.shiftLeft(right.intValue()).longValue());
            case RSHIFT:
                return new IRConst(left.longValue() >>> right.longValue());
            case ARSHIFT:
                return new IRConst(left.longValue() >> right.longValue());
            case EQ:
                return (left.equals(right)) ? new IRConst(1) : new IRConst(0);
            case NEQ:
                return (left.equals(right)) ? new IRConst(0) : new IRConst(1);
            case LT:
                return (left.compareTo(right) < 0) ? new IRConst(1)
                        : new IRConst(0);
            case GT:
                return (left.compareTo(right) > 0) ? new IRConst(1)
                        : new IRConst(0);
            case LEQ:
                return (left.compareTo(right) <= 0) ? new IRConst(1)
                        : new IRConst(0) ;
            case GEQ:
                return (left.compareTo(right) >= 0) ? new IRConst(1)
                        : new IRConst(0);
            default: // should not reach here
                throw new RuntimeException("Please contact jk2227@cornell.edu");
        }
    }

//    @Override
//    public void fixpoint(Direction direction) {
//        Map<Integer, CFGNode> nodes = graph.getNodes();
//        LinkedList<CFGNode> worklist = new LinkedList<>();
//        // Initialize the in and out of each node and add it to the worklist
//        for (CFGNode node : nodes.values()) {
//            // Initialize all in and out to be top
//            node.setIn(top.copy());
//            //node.setOut(top.copy());
//            //worklist.add(node);
//        }
//        worklist.add(nodes.values().iterator().next());
//        while (!worklist.isEmpty()) {
//            // We find the fixpoint using the worklist algorithm
//            System.out.println(worklist.size());
//            CFGNode node = worklist.remove();
//            //LatticeElement oldIn = node.getIn().copy();
//            List<CFGNode> old = new LinkedList<>();
//            old.addAll(node.getSuccessors());
//            transfer(node);
//            for(int i = 0 ; i < node.getSuccessors().size(); i++) {
//                if (!(node.getSuccessors().get(i).equals(old.get(i)))) {
//                    worklist.add(node);
//                }
//            }
//        }
//    }

}


