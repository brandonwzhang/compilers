package com.bwz6jk2227esl89ahj34;

import com.AST.*;
import java.util.*;

public class TypeCheckVisitor implements NodeVisitor {
    // Context field
    private Stack<Context> contexts;
    // AST field (that we build up as we visit) TODO

    // Local variable that stores current function in scope with a stack, used for return statements
    private Stack<Identifier> currentFunction = new Stack<>();

    // Final types, for ease of checking equality
    private final VariableType UNIT_TYPE = new VariableType(PrimitiveType.UNIT, 0);
    private final VariableType VOID_TYPE = new VariableType(PrimitiveType.VOID, 0);
    private final VariableType INT_TYPE = new VariableType(PrimitiveType.INT, 0);
    private final VariableType BOOL_TYPE = new VariableType(PrimitiveType.BOOL, 0);

    private static BinaryOperator[] int_binary_operator_int = new BinaryOperator[] {
            BinaryOperator.PLUS, BinaryOperator.MINUS, BinaryOperator.TIMES, BinaryOperator.DIVIDE, BinaryOperator.MODULO,
            BinaryOperator.HIGH_MULT
    };

    private static BinaryOperator[] int_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL, BinaryOperator.LT, BinaryOperator.LEQ,
            BinaryOperator.GT, BinaryOperator.GEQ
    };

    private static BinaryOperator[] bool_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL, BinaryOperator.AND, BinaryOperator.OR
    };

    private static BinaryOperator[] array_binary_operator_bool = new BinaryOperator[] {
            BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL
    };

    private final Set<BinaryOperator> INT_BINARY_OPERATOR_INT = new HashSet<>(Arrays.asList(int_binary_operator_int));
    private final Set<BinaryOperator> INT_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(int_binary_operator_bool));
    private final Set<BinaryOperator> BOOL_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(bool_binary_operator_bool));
    private final Set<BinaryOperator> ARRAY_BINARY_OPERATOR_BOOL = new HashSet<>(Arrays.asList(array_binary_operator_bool));

    public TypeCheckVisitor() {
        contexts = new Stack<>();
        // initialize first context with length function
        Context initContext = new Context();
        List<VariableType> lengthArgType = Arrays.asList(new VariableType(PrimitiveType.UNIT, 0));
        VariableTypeList lengthReturnType =
                new VariableTypeList(Arrays.asList(new VariableType(PrimitiveType.INT, 0)));
        initContext.put(new Identifier("length"), new FunctionType(lengthArgType, lengthReturnType));

        contexts.push(initContext);
    }

    public void visit(ArrayIndex node) {
        node.getArrayRef().accept(this);
        Type arrayType = node.getType();
        if (arrayType instanceof VariableTypeList) {
            List<VariableType> returnTypes = ((VariableTypeList)(arrayType)).getVariableTypeList();
            if (returnTypes.size() != 1) {
                throw new TypeException("TODO this isn't an array so we can't index");
            } else {
                VariableType array = returnTypes.get(0);
                if(array.getNumBrackets() < 1) {
                    throw new TypeException("TODO this isn't an array so we can't index");
                }
                node.setType(new VariableType(array.getPrimitiveType(), array.getNumBrackets()-1));
            }
        } else if (arrayType instanceof VariableType) {
            VariableType array = (VariableType)(arrayType);
            if(array.getNumBrackets() < 1) {
                throw new TypeException("TODO this isn't an array so we can't index");
            }
            node.setType(new VariableType(array.getPrimitiveType(), array.getNumBrackets()-1));
        } else { //arrayType instanceof FuncType
            throw new TypeException("TODO I don't think it should ever get here unless it's an error");
        }
    }

    public void visit(ArrayLiteral node) {
        if (node.getValues().size() == 0) {
            node.setType(new VariableType(PrimitiveType.INT, 1));
            return;
        }

        // check all types equal to the first type
        // TODO: enforce everything is a variable type?
        Type type = null;
        for (Expression e : node.getValues()) {
            e.accept(this);
            if (type == null) {
                type = e.getType();
            } else if (!type.equals(e.getType())) {
                throw new TypeException("TODO");
            }
        }
        assert type != null;

        node.setType(new VariableType(((VariableType) type).getPrimitiveType(), 1));
    }

    public void visit(Assignment node) {
        for (Assignable a : node.getVariables()) {
            a.accept(this);
        }
        node.getExpression().accept(this);

        // if LHS has more than one element, RHS must be VariableTypeList
        if (node.getVariables().size() > 1 && !(node.getExpression().getType() instanceof VariableTypeList)) {
            throw new TypeException("TODO");
        }
        else if (node.getVariables().size() == 1) {
            if (!node.getVariables().get(0).getType().equals(node.getExpression().getType())) {
                throw new TypeException("TODO");
            }
        }

        else {
            // TODO: multiple assign on left
            VariableTypeList RHS = (VariableTypeList) node.getExpression().getType();
            if (node.getVariables().size() != RHS.getVariableTypeList().size()) {
                throw new TypeException("TODO");
            }
            for (int i = 0; i < node.getVariables().size(); i++) {
                Type leftType = node.getVariables().get(i).getType();
                Type rightType = RHS.getVariableTypeList().get(i);
                if (!leftType.equals(rightType)) {
                    throw new TypeException("TODO");
                }
            }
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Binary node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (!node.getLeft().getType().equals(node.getRight().getType())) {
            throw new TypeException("TODO invalid binary operator");
        } else {
            BinaryOperator binop = node.getOp();
            boolean isInteger = node.getLeft().getType().equals(INT_TYPE);
            boolean isBool = node.getLeft().getType().equals(BOOL_TYPE);

            boolean valid_int_binary_operator_int = INT_BINARY_OPERATOR_INT.contains(binop) && isInteger;
            boolean valid_int_binary_operator_bool = INT_BINARY_OPERATOR_BOOL.contains(binop) && isInteger;
            boolean valid_bool_binary_operator_bool = BOOL_BINARY_OPERATOR_BOOL.contains(binop) && isBool;

            if (valid_int_binary_operator_int) {
                node.setType(new VariableType(PrimitiveType.INT, 0));
            } else if (valid_int_binary_operator_bool) {
                node.setType(new VariableType(PrimitiveType.BOOL, 0));
            } else if (valid_bool_binary_operator_bool) {
                node.setType(new VariableType(PrimitiveType.BOOL, 0));
            } else if (ARRAY_BINARY_OPERATOR_BOOL.contains(binop) && !isBool && !isInteger) {
                node.setType(new VariableType(PrimitiveType.BOOL, 0));
            } else if (binop.equals(BinaryOperator.PLUS) && !isBool && !isInteger) {
                VariableType leftType = (VariableType) node.getLeft().getType();
                node.setType(new VariableType(leftType.getPrimitiveType(), leftType.getNumBrackets()));
            } else {
                throw new TypeException("TODO invalid binaryoperator");
            }
        }
    }

    public void visit(BlockList node) {
        for (Block block : node.getBlockList()) {
            block.accept(this);
        }
    }

    public void visit(BooleanLiteral node) {
        node.setType(new VariableType(PrimitiveType.BOOL, 0));
    }
    public void visit(CharacterLiteral node) {
        node.setType(new VariableType(PrimitiveType.INT, 0));
    }

    public void visit(FunctionBlock node) {
        Context newContext = new Context(contexts.peek());
        contexts.push(newContext);
        node.getBlockList().accept(this);
        node.getReturnStatement().accept(this);

        // sanity check
        if (!node.getBlockList().getType().equals(UNIT_TYPE) || !node.getReturnStatement().getType().equals(VOID_TYPE)) {
            throw new TypeException("TODO");
        }
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
        // TODO: check types, and set this type

        contexts.pop();
    }

    public void visit(FunctionCall node) {
        Context context = contexts.peek();
        if (!context.containsKey(node.getIdentifier())) {
            throw new TypeException("TODO: function not defined");
        }

        // casting to variabletype occurs here...
        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            argumentTypes.add((VariableType) argument.getType());
        }

        FunctionType thisFuncType = (FunctionType) context.get(node.getIdentifier());
        FunctionType funcType = new FunctionType(argumentTypes, thisFuncType.getReturnTypeList());
        if (!thisFuncType.equals(funcType)) {
            throw new TypeException("TODO: argument types do not match with procedure definition");
        }

        node.setType(thisFuncType.getReturnTypeList());
    }

    public void visit(FunctionDeclaration node) {
        // TODO: do we care about the other fields?

        this.currentFunction.push(new Identifier(node.getIdentifier().getName()));
        node.getMethodBlock().accept(this);

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Identifier node) {
        // check if identifier is in context
        Type type = contexts.peek().get(node);
        if (type == null) {
            throw new TypeException("TODO"); // identifier not in context
        }
        node.setType(type);
    }

    public void visit(IfStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().equals(BOOL_TYPE)) {
            throw new TypeException("TODO: guard not a bool");
        }
        node.getTrueBlock().accept(this);

        if (node.getFalseBlock().isPresent()) {
            node.getFalseBlock().get().accept(this);
            PrimitiveType r1 = ((VariableType) node.getTrueBlock().getType()).getPrimitiveType();
            PrimitiveType r2 = ((VariableType) node.getFalseBlock().get().getType()).getPrimitiveType();
            node.setType(new VariableType(Util.lub(r1,r2), 0));
        } else {
            node.setType(new VariableType(PrimitiveType.UNIT, 0));
        }
    }

    public void visit(IntegerLiteral node) {
        node.setType(new VariableType(PrimitiveType.INT, 0));
    }

    public void visit(ProcedureBlock node) {
        Context newContext = new Context(contexts.peek());
        contexts.push(newContext);
        node.getBlockList().accept(this);
        contexts.pop();
    }

    public void visit(ProcedureCall node) {
        Context context = contexts.peek();
        if (!context.containsKey(node.getIdentifier())) {
            throw new TypeException("TODO: procedure not defined");
        }

        // variabletype casting again
        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            argumentTypes.add((VariableType) argument.getType());
        }

        FunctionType funcType = new FunctionType(argumentTypes, new VariableTypeList(new ArrayList<>()));
        if (!funcType.equals(context.get(node.getIdentifier()))) {
            throw new TypeException("TODO: argument types do not match with procedure definition");
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Program node) {
        Context globalContext = contexts.peek();

        // Imports all functions from interface file into the context
        for (UseStatement useStatement : node.getUseBlock()) {
            useStatement.accept(this);
        }

        // first pass collects all function types, adds to context
        for (FunctionDeclaration funcDec : node.getFuncDecs()) {
            // TODO: is shadowing of functions disallowed?
            Identifier funcName = funcDec.getIdentifier();
            FunctionType funcType = funcDec.getFunctionType();
            contexts.peek().put(funcName, funcType);
        }

        // second pass typechecks all the function bodies
        for (FunctionDeclaration funcDec : node.getFuncDecs()) {
            funcDec.accept(this);
        }

        // Shouldn't be needed if all children typecheck correctly
        for (FunctionDeclaration funcDec : node.getFuncDecs()) {
            if (!funcDec.getType().equals(PrimitiveType.UNIT)) {
                throw new TypeException("TODO");
            }
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0)); // TODO: do we need this?
    }

    public void visit(ReturnStatement node) {
        // Checks if the return types match those outlined in the function declaration
        FunctionType returnType = (FunctionType)contexts.peek().get(currentFunction);

        List<VariableType> functionReturnTypes = returnType.getReturnTypeList().getVariableTypeList();
        List<Expression> returnTypes = node.getValues();

        // check if they are both the same size
        if (functionReturnTypes == null || functionReturnTypes.size() == 0) {
            if ((returnTypes == null) || (returnTypes.size() == 0)) {
                throw new TypeException("TODO procedure is returning something");
            }
        }

        // precondition: they are the same length
        if(functionReturnTypes.size() != returnTypes.size()) {
            throw new TypeException("TODO return values do not match the types in function declaration");
        }
        for (int i = 0; i < returnTypes.size(); i++) {
            if (!functionReturnTypes.get(i).equals(returnTypes.get(i))) {
                throw new TypeException("TODO");
            }
        }

        node.setType(new VariableType(PrimitiveType.VOID, 0));
    }

    public void visit(StringLiteral node) {
        node.setType(new VariableType(PrimitiveType.INT, 1));
    }

    public void visit(TypedDeclaration node) {
        Identifier identifier = node.getIdentifier();
        Context context = contexts.peek();
        if (context.containsKey(identifier)) {
            throw new TypeException("TODO: cannot shadow variables");
        }
        context.put(identifier, node.getDeclarationType());
        node.setType(node.getDeclarationType());
    }

    public void visit(Unary node) {
        node.getExpression().accept(this);
        UnaryOperator unop = node.getOp();
        if (node.getExpression().getType().equals(INT_TYPE) && unop == UnaryOperator.MINUS) {
            node.setType(new VariableType(PrimitiveType.INT, 0));
        } else if (node.getExpression().getType().equals(BOOL_TYPE) && unop == UnaryOperator.NOT) {
            node.setType(new VariableType(PrimitiveType.BOOL, 0));
        } else {
            throw new TypeException("TODO invalid unary operator");
        }

    }

    public void visit(Underscore node) {
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(UseStatement node) {
        // find interface files
        // parse the function names and types
        // add them to the symbol table at top of stack
    }

    public void visit(WhileStatement node) {
        node.getGuard().accept(this);
        if (!node.getGuard().getType().equals(BOOL_TYPE)) {
            throw new TypeException("TODO");
        }
        node.getBlock().accept(this);

        if (!node.getBlock().getType().equals(UNIT_TYPE) || !node.getBlock().getType().equals(VOID_TYPE)) {
            throw new TypeException("TODO");
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }
}