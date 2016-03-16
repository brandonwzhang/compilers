package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.*;

import java.math.BigInteger;
import java.util.*;

//TODO: remove the exceptions for over/underflow
public class BinarySymbol {
    static BinaryOperator[] int_binary_operator_int = new BinaryOperator[] {
            BinaryOperator.PLUS, BinaryOperator.MINUS, BinaryOperator.TIMES, BinaryOperator.DIVIDE, BinaryOperator.MODULO,
            BinaryOperator.HIGH_MULT
    };

    static BinaryOperator[] int_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL, BinaryOperator.LT, BinaryOperator.LEQ,
            BinaryOperator.GT, BinaryOperator.GEQ
    };

    static BinaryOperator[] bool_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL, BinaryOperator.AND, BinaryOperator.OR
    };

    static BinaryOperator[] array_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL
    };

    static final Set<BinaryOperator> INT_BINARY_OPERATOR_INT = new HashSet<>(Arrays.asList(int_binary_operator_int));
    static final Set<BinaryOperator> INT_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(int_binary_operator_bool));
    static final Set<BinaryOperator> BOOL_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(bool_binary_operator_bool));
    static final Set<BinaryOperator> ARRAY_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(array_binary_operator_bool));

    private static final VariableType INT_TYPE = new VariableType(PrimitiveType.INT, 0);
    private static final VariableType BOOL_TYPE = new VariableType(PrimitiveType.BOOL, 0);

    /**
     * precondition: the code has already gone through the
     * typechecking phase, so we know that b is a valid Binary object
     * that abides by the rules of Xi
     * @param b
     * @return
     */
    public static Expression compute(Binary b) {
        BinaryOperator binop = b.getOp();
        if (b.getLeft() instanceof IntegerLiteral &&
                b.getRight() instanceof IntegerLiteral) {
            IntegerLiteral left = (IntegerLiteral)(b.getLeft());
            IntegerLiteral right = (IntegerLiteral)(b.getRight());
            BigInteger leftValueBigInt = new BigInteger(left.getValue());
            BigInteger rightValueBigInt = new BigInteger(right.getValue());
            if (BinarySymbol.INT_BINARY_OPERATOR_INT.contains(binop)) {
                switch (binop) {
                    case PLUS:
                        BigInteger sumBigInt = leftValueBigInt.add(rightValueBigInt);
                        return new IntegerLiteral(""+sumBigInt.longValue());
                    case MINUS:
                        BigInteger subtractionBigInt = leftValueBigInt.subtract(rightValueBigInt);
                        return new IntegerLiteral(""+subtractionBigInt.longValue());
                    case TIMES:
                        BigInteger multipleBigInt = leftValueBigInt.multiply(rightValueBigInt);
                        return new IntegerLiteral(""+multipleBigInt.longValue());
                    case DIVIDE:
                        // for divide by 0, we believe constant folding over it
                        // doesn't make any sense, so we just return the
                        // Binary expression as is to avoid throwing an error
                        if (rightValueBigInt.longValue() == 0 ) {
                            return b;
                        }
                        BigInteger divideBigInt = leftValueBigInt.divide(rightValueBigInt);
                        return new IntegerLiteral(""+divideBigInt.longValue());
                    case MODULO:
                        BigInteger moduloBigInt = leftValueBigInt.remainder(rightValueBigInt);
                        return new IntegerLiteral(""+moduloBigInt.longValue());
                    case HIGH_MULT:
                        BigInteger highMultBigInt = leftValueBigInt.multiply(rightValueBigInt).shiftRight(64);
                        return new IntegerLiteral(highMultBigInt.toString());
                    default:
                        return b; //shouldn't reach here
                }
            } else if (BinarySymbol.INT_BINARY_OPERATOR_BOOL.contains(binop)) {
                switch (binop) {
                    case EQUAL:
                        return new BooleanLiteral(leftValueBigInt.equals(rightValueBigInt));
                    case NOT_EQUAL:
                        return new BooleanLiteral(!leftValueBigInt.equals(rightValueBigInt));
                    case LT:
                        return new BooleanLiteral(leftValueBigInt.compareTo(rightValueBigInt) < 0);
                    case LEQ:
                        return new BooleanLiteral(leftValueBigInt.compareTo(rightValueBigInt) <= 0);
                    case GT:
                        return new BooleanLiteral(leftValueBigInt.compareTo(rightValueBigInt) > 0);
                    case GEQ:
                        return new BooleanLiteral(leftValueBigInt.compareTo(rightValueBigInt) >= 0);
                    default:
                        return b; //should never reach here
                }
            }
        }
        else if (b.getLeft() instanceof BooleanLiteral
                && b.getRight() instanceof BooleanLiteral) {
            Boolean leftValue = ((BooleanLiteral)b.getLeft()).getValue();
            Boolean rightValue = ((BooleanLiteral)b.getRight()).getValue();
            switch(binop) {
                case EQUAL:
                    return new BooleanLiteral(leftValue.equals(rightValue));
                case NOT_EQUAL:
                    return new BooleanLiteral(!leftValue.equals(rightValue));
                case AND:
                    if (leftValue.booleanValue() == true)
                        return new BooleanLiteral(rightValue);
                    else
                        return new BooleanLiteral(false);
                case OR:
                    if (leftValue.booleanValue() == true)
                        return new BooleanLiteral(true);
                    else
                        return new BooleanLiteral(rightValue);
                default:
                    return b; //should never reach here
            }

        }
        else if (b.getLeft() instanceof BooleanLiteral) {
            Boolean leftValue = ((BooleanLiteral) b.getLeft()).getValue();
            switch (binop) {
                case AND:
                    if (leftValue.booleanValue() == true)
                        return b.getRight();
                    else
                        return new BooleanLiteral(false);
                case OR:
                    if (leftValue.booleanValue() == true)
                        return new BooleanLiteral(true);
                    else
                        return b.getRight();
                default:
                    return b;
            }
        }
        else if (binop == BinaryOperator.PLUS
                && b.getLeft() instanceof ArrayLiteral
                && b.getRight() instanceof ArrayLiteral
                ) {
            ArrayLiteral left = (ArrayLiteral)(b.getLeft());
            ArrayLiteral right = (ArrayLiteral)(b.getRight());
            Expression result;
            List<Expression> arrayLiteral = new LinkedList<>(left.getValues());
            arrayLiteral.addAll(right.getValues());

            result = new ArrayLiteral(arrayLiteral);
            return result;
        }
        else if (ARRAY_BINARY_OPERATOR_BOOL.contains(binop)
            && b.getLeft() instanceof ArrayLiteral
            && b.getRight() instanceof ArrayLiteral) {
            List<Expression> left = ((ArrayLiteral)(b.getLeft())).getValues();
            List<Expression> right = ((ArrayLiteral)(b.getRight())).getValues();
            Expression result;
            switch(binop) {
                case EQUAL:
                    result = new BooleanLiteral(left.equals(right));
                    return result;
                case NOT_EQUAL:
                    result = new BooleanLiteral(!left.equals(right));
                    return result;
                default:
                    return b; //should never reach here
            }
        } else {
                return b;
        }
        return b;
    }
}
