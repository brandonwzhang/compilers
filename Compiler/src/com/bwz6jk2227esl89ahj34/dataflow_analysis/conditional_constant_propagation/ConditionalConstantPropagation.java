package com.bwz6jk2227esl89ahj34.dataflow_analysis.conditional_constant_propagation;


import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

import java.math.BigInteger;
import java.util.*;

public class ConditionalConstantPropagation extends DataflowAnalysis {

    public ConditionalConstantPropagation(IRSeq seq) {
        super(seq, Direction.FORWARD, new UnreachableValueTuplesPair(findAllTemps(seq)));
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
    // unless it is the root; then the root will be changed
    // such that unreachable = false
    //
    // for each node, we read apply the transfer function, and place
    // the result of the transfer function onto the out.
    //
    // if there is already something on the out edge then we will MEET
    //
    // then, we will transmit the information on out to the ins of its
    // successors
    //
    // if there is already something on the successor's in edge then we
    // will MEET
    public void transfer(CFGNode node) {
        // this analysis is being done on the IR level so
        // this is a sanity check to make sure we are at the right place
        assert node instanceof CFGNodeIR;

        // we first unwrap the IR stmt contained by node
        IRStmt stmt = ((CFGNodeIR)node).getStatement();

        // and extract its in
        UnreachableValueTuplesPair in = (UnreachableValueTuplesPair) node.getIn();
        // this checks whether the node is the root or not
        if (node.getPredecessors().size() == 0) {
            in.setUnreachable(false);
        }

        // otherwise, it is not the root
        // therefore, it should already have information on the "in" edge
        // if this node is reachable or not -- if it is unreachable
        // we do not have to do anything
        if (in.isUnreachable()) {
                // for the branch we do not take, we have to prepare
                // new information to send
                Map<String, LatticeElement> infoForDead = new HashMap<>();
                for (String key : in.getValueTuples().keySet()) {
                    infoForDead.put(key, new LatticeTop());
                }

                UnreachableValueTuplesPair deadBranchInfo =
                        new UnreachableValueTuplesPair(true, infoForDead);
                node.setOut(deadBranchInfo.copy());

                // now we disperse this information to the successor
                for (CFGNode successor : node.getSuccessors()) {
                    setIn(successor, (UnreachableValueTuplesPair) node.getOut().copy());
                }
                return;
        }

        // once we are here, we know that this node is reachable
        // therefore, we perform our analysis

        // we will update the newMap as we perform the analysis
        // if no updates are made then we are basically passing the map given
        // by the "in"
        Map<String, LatticeElement> newMap = new HashMap<>(in.getValueTuples());

        // now we exhaust the possible forms that statements can take
        // however, the only ones we should consider are statements
        // of the following form:
        //
        // When LHS is a IRTemp...
        // LHS = RHS ==> if RHS is a IRConst then we can set LHS to RHS
        //
        // if RHS is a IRBinOp, then we attempt to evaluate the IRBinOp
        // if IRBinOp is able to be calculated to a IRConst, we treat it like
        // an IRConst, but otherwise we set it as BOTTOM
        //
        // if RHS is a IRTemp, then we see if that IRTemp is bound to any value
        // and set it as that value
        //
        // if RHS is a IRCall then we set LHS to BOTTOM because it can be anything
        //
        // if RHS is a IRMem then we set LHS to BOTTOM because MEM is hard
        if (stmt instanceof IRMove) { // LHS = RHS

            //we first cast the stmt to an IRMove
            IRMove castedStmt = (IRMove) stmt;

            // we get the LHS
            IRExpr LHS = castedStmt.target();
            if (LHS instanceof IRTemp) {
                String nameLHS = ((IRTemp)LHS).name();
                IRExpr RHS = castedStmt.expr();
                if (RHS instanceof IRConst) {
                    newMap.put(nameLHS, new Value((IRConst)RHS));
                } else if (RHS instanceof IRTemp) {
                    String nameRHS = ((IRTemp)RHS).name();
                    newMap.put(nameLHS, newMap.get(nameRHS));
                } else if (RHS instanceof IRBinOp) {
                    IRBinOp castedRHS = (IRBinOp) RHS;
                    IRExpr result = compute(castedRHS, newMap);

                    // if we are unable to simplify the bin op expression,
                    // then it can take multiple forms, so LHS -> BOTTOM
                    if (result instanceof IRBinOp) {
                        newMap.put(nameLHS, new LatticeBottom());
                    } else if (result instanceof IRConst) {
                        // otherwise we attempt to associate it with LHS in the map
                        newMap.put(nameLHS, new Value((IRConst)result));
                    } else { // shouldn't reach here
                        System.out.println("RHS is: " + RHS);
                        throw new RuntimeException("Please contact jk2227@cornell.edu");
                    }
                } else if (RHS instanceof IRCall || RHS instanceof IRMem) {
                    newMap.put(nameLHS, new LatticeBottom());
                } else { // should not reach here
                    System.out.println("RHS is: "+ RHS);
                    throw new RuntimeException("Please contact jk2227@cornell.edu");
                }
            }
            // the out is set to the updated information
            node.setOut(new UnreachableValueTuplesPair(in.isUnreachable(), newMap));

            // now we disperse this information to the successor
            for (CFGNode successor : node.getSuccessors()) {
                setIn(successor, (UnreachableValueTuplesPair) node.getOut().copy());
            }
        } else if (stmt instanceof IRCJump) { // if (...)
            // we cast stmt to a IRCJump
            IRCJump castedStmt = (IRCJump) stmt;
            IRExpr guard = computeGuard(castedStmt.expr(), newMap);

            // if the guard is a IRConst we can kill one of the branches
            // thus we would disperse different information
            if (guard instanceof IRConst) {
                // as a sanity check, we assert that the size of
                // successors must be 2
                assert node.getSuccessors().size() == 2;

                node.setOut(node.getIn().copy()); // no information is changed for
                                         // branch that we always take

                // for the branch we do not take, we have to prepare
                // new information to send
                Map<String, LatticeElement> infoForDead = new HashMap<>();
                for (String key : newMap.keySet()) {
                    infoForDead.put(key, new LatticeTop());
                }

                UnreachableValueTuplesPair deadBranchInfo =
                        new UnreachableValueTuplesPair(true, infoForDead);

                // the false branch is always the first successor
                // and the false branch is killed if the guard evaluated to 1
                if (((IRConst)guard).value() == 1) {
                    setIn(node.getSuccessors().get(0), (UnreachableValueTuplesPair) (deadBranchInfo.copy()));
                    setIn(node.getSuccessors().get(1),
                            (UnreachableValueTuplesPair) node.getOut().copy());
                } else { // otherwise, we pass the regular info to the false
                        // branch and the dead info to true branch
                    setIn(node.getSuccessors().get(1), (UnreachableValueTuplesPair) (deadBranchInfo.copy()));
                    setIn(node.getSuccessors().get(0),
                            (UnreachableValueTuplesPair) node.getOut().copy());
                }

            } else { // we disperse the same informations to the successor
                node.setOut(node.getIn().copy());
                // now we disperse this information to the successor
                for (CFGNode successor : node.getSuccessors()) {
                    setIn(successor, (UnreachableValueTuplesPair) node.getOut().copy());
                }
            }
        } else { // only case I think can fall through here is an IRJump
            // the out is set to in because no information was changed
            node.setOut(node.getIn().copy());
            // now we disperse this information to the successor
            for (CFGNode successor : node.getSuccessors()) {
                setIn(successor, (UnreachableValueTuplesPair) node.getOut().copy());
            }

        }



    }

    // 2 + top = 2 ; 2 + 3 = bottom; 2 + 2 = 2; bottom + anything = bottom
    public LatticeElement valueMeet(LatticeElement e1, LatticeElement e2) {
        assert (e1 != null && e2 != null);
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

    // computes binop expression to const
    public static IRExpr compute(IRBinOp bop, Map<String, LatticeElement> map) {

        IRExpr leftBop = bop.left();
        IRExpr rightBop = bop.right();

        // if either left or right is a IR binop then
        // we recurse on these first
        if (leftBop instanceof IRBinOp) {
            leftBop = compute((IRBinOp)leftBop, map);
        }

        if (rightBop instanceof IRBinOp) {
            rightBop = compute((IRBinOp)rightBop, map);
        }

        // if after the recursion either of them are still binops
        // then we return bop
        if (leftBop instanceof IRBinOp || rightBop instanceof IRBinOp) {
            return bop;
        }

        // if left and right are IRTemp, we attempt to convert it to
        // a IRConst
        if (leftBop instanceof IRTemp) {
            String leftName = ((IRTemp) leftBop).name();
            if (map.get(leftName) instanceof Value) {
                leftBop = ((Value) (map.get(leftName))).getValue();
            }
        }

        if (rightBop instanceof IRTemp) {
            String rightName = ((IRTemp) rightBop).name();
            if (map.get(rightName) instanceof Value) {
                rightBop = ((Value) (map.get(rightName))).getValue();
            }
        }

        //if the attempt fails, we return bop
        if (!(leftBop instanceof IRConst && rightBop instanceof IRConst)) {
            return bop;
        }

        // otherwise, they are both IRConst, so we attempt to simplify


        BigInteger left = new BigInteger(""+((IRConst)(leftBop)).value());
        BigInteger right = new BigInteger(""+((IRConst)(rightBop)).value());
        switch(bop.opType()) {
            case ADD:
                return new IRConst(left.add(right).longValue());
            case SUB:
                return new IRConst(left.subtract(right).longValue());
            case MUL:
                return new IRConst(left.multiply(right).longValue());
            case HMUL:
                return new IRConst(left.multiply(right).longValue() >> 64);
            case DIV: // TODO: divide by zero
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

    public static IRExpr computeGuard (IRExpr guard, Map<String, LatticeElement> map) {
        if (guard instanceof IRTemp) {
            LatticeElement guardValue = map.get(((IRTemp)guard).name());
            if (guardValue instanceof LatticeTop || guardValue instanceof LatticeBottom) {
                return guard;
            } else {
                return ((Value) guardValue).getValue();
            }
        } else if (guard instanceof IRBinOp) {
            return compute((IRBinOp)guard, map);
        } else if (guard instanceof IRConst || guard instanceof IRCall || guard instanceof IRMem) {
            return guard;
        } else { // should not reach here...
            throw new RuntimeException("Please contact jk2227@cornell.edu");
        }
    }


    // we set the successor's in as newIn
    public void setIn(CFGNode successor, UnreachableValueTuplesPair newIn) {
        if (successor.getIn() == null) { // if in is null prior, then
            successor.setIn(newIn);  // in = newIn
        } else { // otherwise we "meet" it
            Set<LatticeElement> ins = new HashSet<>(Arrays.asList(successor.getIn(), newIn));
            successor.setIn(meet(ins));
        }
    }

    // meet operator for CCP
    public UnreachableValueTuplesPair meet(Set<LatticeElement> elements) {
        if (elements.size() == 1) { // would only happen if set had duplicate lattice element
            return (UnreachableValueTuplesPair) elements.iterator().next();
        }
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

    @Override
    public void fixpoint(Direction direction) {
        Map<Integer, CFGNode> nodes = graph.getNodes();
        LinkedList<CFGNode> worklist = new LinkedList<>();
        // Initialize the in and out of each node and add it to the worklist
        for (CFGNode node : nodes.values()) {
            // Initialize all in and out to be top
            node.setIn(initial.copy());
            node.setOut(initial.copy());
        }
        //worklist.add(nodes.values().iterator().next());
        worklist.addAll(nodes.values());
        while (!worklist.isEmpty()) {
            // We find the fixpoint using the worklist algorithm
            CFGNode node = worklist.remove();
            // LatticeElement oldOut = node.getOut().copy();
            LatticeElement oldIn = node.getIn().copy();
            LatticeElement oldOut = node.getOut().copy();
            transfer(node);
            LatticeElement newOut = node.getOut().copy();

            boolean inBool = ((UnreachableValueTuplesPair) node.getIn()).isUnreachable();
            boolean outBool = ((UnreachableValueTuplesPair) node.getOut()).isUnreachable();

            assert inBool == outBool;

            if (!oldIn.equals(newOut) || !oldOut.equals(newOut)) {
                worklist.addAll(node.getSuccessors());
            }
        }
    }

}


