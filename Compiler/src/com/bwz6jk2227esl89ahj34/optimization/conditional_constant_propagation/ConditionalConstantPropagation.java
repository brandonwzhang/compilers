package com.bwz6jk2227esl89ahj34.optimization.conditional_constant_propagation;

import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.optimization.*;
import com.bwz6jk2227esl89ahj34.optimization.CFGNode.NodeType;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by jihunkim on 4/19/16.
 */
public class ConditionalConstantPropagation extends DataflowAnalysis {


    // assumption: ins will always be defined appropriately
    public void transfer(CFGNode element) {
        assert element.getNodeType() == NodeType.IR;

        IRStmt node = element.getIrstmt();
        UnreachableValueTuplesPair in =
                (UnreachableValueTuplesPair) element.getIn();
        Map<IRTemp, LatticeElement> newMap = new HashMap<>(in.getValueTuples());

        if (node instanceof IRCJump) { // if (...)
            IRCJump castedNode = (IRCJump) node;
            IRExpr guard = castedNode.expr();
            if (guard instanceof IRTemp) {
                IRTemp castedGuard = (IRTemp) guard;
                LatticeElement value = newMap.get(castedGuard);
                if (value instanceof Value) {
                    guard = ((Value) value).getValue();
                }
            } else if (guard instanceof IRBinOp) {
                IRBinOp castedGuard = (IRBinOp) guard;
                IRExpr left = castedGuard.left();
                IRExpr right = castedGuard.right();

                // can we substitute in const values for left and right?
                if (left instanceof IRTemp
                        && newMap.get(left) instanceof Value) {
                    left = ((Value)(newMap.get(left))).getValue();
                }
                if (right instanceof IRTemp
                        && newMap.get(right) instanceof Value) {
                    right = ((Value)(newMap.get(right))).getValue();
                }

                // if we can then simplify the binop!
                if (left instanceof IRConst && right instanceof IRConst) {
                    if (castedGuard.opType() != OpType.DIV ||
                            ((IRConst)right).value() != 0) {
                        guard = computeBinOp(
                                new IRBinOp(castedGuard.opType(), left, right)
                        );
                    }
                }
            }

            if (guard instanceof IRConst) {
                IRConst castedGuard = (IRConst) guard;
                UnreachableValueTuplesPair leftPair;
                UnreachableValueTuplesPair rightPair;
                assert element.getSuccessors().size() == 2;
                if (castedGuard.value() == 1) { // if (...) always goes to true
                    // then we have to kill the false branch by sending
                    // (true, (T,..,T))
                    // and send down appropriate info to true branch
                    leftPair = new UnreachableValueTuplesPair(false, newMap);
                    Map<IRTemp, LatticeElement> tops = new HashMap<>();
                    for (IRTemp temp : newMap.keySet()) {
                        tops.put(temp, new LatticeTop());
                    }
                    rightPair = new UnreachableValueTuplesPair(true, tops);
                } else { // if (...) always goes to false
                    rightPair = new UnreachableValueTuplesPair(false, newMap);
                    Map<IRTemp, LatticeElement> tops = new HashMap<>();
                    for (IRTemp temp : newMap.keySet()) {
                        tops.put(temp, new LatticeTop());
                    }
                    leftPair = new UnreachableValueTuplesPair(true, tops);
                }
                // now we set the ins of the successor
                // however, we do a null check; if the in is not null, then
                // we meet it with whatever is already present at the in
                int count = 0; // count = 0 -> left/true branch
                              // otherwise right/false branch
                for (CFGNode successor : element.getSuccessors()) {
                    setIn(successor, count == 0 ? leftPair : rightPair);
                    count++;
                }
            } else {
                for (CFGNode successor : element.getSuccessors()) {
                    setIn(successor, new UnreachableValueTuplesPair(false, newMap));
                }
            }
        } else if (node instanceof IRMove) {
            IRMove castedNode = (IRMove) node;
            if (castedNode.target() instanceof IRTemp) {
                IRTemp castedTarget = (IRTemp) castedNode.target();
                String variable = castedTarget.label();
                if (castedNode.expr() instanceof IRConst) {
                    Value val = new Value((IRConst) castedNode.expr());
                    newMap.put(castedTarget, valueMeet(newMap.get(variable), val));
                } else if (castedNode.expr() instanceof IRCall) {
                    newMap.put(castedTarget, new LatticeBottom());
                } else if (castedNode.expr() instanceof IRTemp) {
                    newMap.put(castedTarget, newMap.get((IRTemp) castedNode.expr()));
                } else {
                    throw new RuntimeException("Please contact jk2227@cornell.edu");
                }
            }
            for (CFGNode successor : element.getSuccessors()) {
                setIn(successor, new UnreachableValueTuplesPair(false, newMap));
            }
        } else {
            for (CFGNode successor : element.getSuccessors()) {
                setIn(successor, new UnreachableValueTuplesPair(false, newMap));
            }
        }
    }

    public void setIn(CFGNode successor, UnreachableValueTuplesPair newIn) {
        if (successor.getIn() == null) {
            successor.setIn(newIn);
        } else {
            Set<LatticeElement> ins = new HashSet<>(Arrays.asList(successor.getIn(), newIn));
            successor.setIn(meet(ins));
        }
    }

    //TODO: add assert statements and spec
    public UnreachableValueTuplesPair meet(Set<LatticeElement> elements) {
        assert elements.size() >= 2;
        Iterator<LatticeElement> iterator = elements.iterator();
        UnreachableValueTuplesPair accumulator = (UnreachableValueTuplesPair) iterator.next();
        while(iterator.hasNext()) {
            UnreachableValueTuplesPair next = (UnreachableValueTuplesPair) iterator.next();
            accumulator.setUnreachable(accumulator.isUnreachable()
                    && next.isUnreachable());
            Map<IRTemp, LatticeElement> accumulatorMap = accumulator.getValueTuples();
            Map<IRTemp, LatticeElement> nextMap = accumulator.getValueTuples();
            for (IRTemp key : accumulatorMap.keySet()) {
                accumulatorMap.put(key,
                        valueMeet(accumulatorMap.get(key), nextMap.get(key)));
            }
            accumulator.setValueTuples(accumulatorMap);
        }
        return accumulator;
    }

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
private IRConst computeBinOp(IRBinOp bop) {
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
}
