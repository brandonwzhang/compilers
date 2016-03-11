package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.BinaryOperator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jihunkim on 3/11/16.
 */
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
}
