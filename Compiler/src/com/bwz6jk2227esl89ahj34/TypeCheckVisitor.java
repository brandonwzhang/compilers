package com.bwz6jk2227esl89ahj34;

import com.AST.*;

import java.util.*;

public class TypeCheckVisitor implements NodeVisitor {
    // Name of the source file. Used for interface checking.
    private String sourceFileName;

    // Path to interface files
    private String libPath;

    // Context field
    private Stack<Context> contexts;

    // AST field (that we build up as we visit) TODO

    // Local variable that stores current function in scope with a stack, used for return statements
    private FunctionType currentFunctionType;

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

    public TypeCheckVisitor(String sourceFileName, String libPath) {
        contexts = new Stack<>();
        // initialize first context with length function
        Context initContext = new Context();
        List<VariableType> lengthArgType = Collections.singletonList(new VariableType(PrimitiveType.INT, 1));
        VariableTypeList lengthReturnType =
                new VariableTypeList(Collections.singletonList(new VariableType(PrimitiveType.INT, 0)));
        initContext.put(new Identifier("length"), new FunctionType(lengthArgType, lengthReturnType));

        contexts.push(initContext);
        this.sourceFileName = sourceFileName;
        this.libPath = libPath;
    }

    public void visit(ArrayIndex node) {
        node.getArrayRef().accept(this);
        node.getIndex().accept(this);

        if (!node.getIndex().getType().equals(INT_TYPE)) {
            throw new TypeException("TODO index is not an integer");
        }

        Type type = node.getArrayRef().getType();
        if (type instanceof VariableType) {
            VariableType arrayType = (VariableType) type;
            if(arrayType.getNumBrackets() < 1) {
                throw new TypeException("TODO this isn't an array so we can't index");
            }
            node.setType(new VariableType(arrayType.getPrimitiveType(), arrayType.getNumBrackets() - 1));

        } else { //arrayType instanceof FuncType of VarTypeList
            throw new TypeException("TODO I don't think it should ever get here unless it's an error");
        }
    }

    public void visit(ArrayLiteral node) {
        if (node.getValues().size() == 0) {
            node.setType(new VariableType(PrimitiveType.INT, 1));
            return;
        }

        // check all types equal to the first type
        Type firstType = null;
        for (Expression e : node.getValues()) {
            e.accept(this);
            Type type = e.getType();
            if (type instanceof VariableType) {
                if (firstType == null) {
                    firstType = type;
                } else if (!firstType.equals(type)) {
                    throw new TypeException("TODO: types in arrayliteral don't match");
                }

            } else {
                throw new TypeException("TODO must be VariableType");
            }
        }
        assert firstType != null;

        VariableType type = (VariableType) firstType;
        node.setType(new VariableType(type.getPrimitiveType(), type.getNumBrackets() + 1));
    }

    public void visit(Assignment node) {
        List<Assignable> variables = node.getVariables();
        for (Assignable a : variables) {
            a.accept(this);
        }
        Expression expression = node.getExpression();
        expression.accept(this);

        // if LHS has more than one element, RHS must be VariableTypeList
        if (expression.getType() instanceof VariableTypeList) {
            VariableTypeList rhs = (VariableTypeList) expression.getType();
            if (variables.size() != rhs.getVariableTypeList().size()) {
                throw new TypeException("TODO sizes don't match");
            }

            for (int i = 0; i < variables.size(); i++) {
                Type leftType = variables.get(i).getType();
                Type rightType = rhs.getVariableTypeList().get(i);

                if (!(leftType instanceof VariableType)) {
                    throw new TypeException("lhs variables must be VariableType");
                }

                if (!leftType.equals(UNIT_TYPE) && !leftType.equals(rightType)) {
                    throw new TypeException("TODO type mismatch");
                }
            }

        } else if (expression.getType() instanceof FunctionType) {
            throw new TypeException("TODO cannot be FunctionType");

        } else if (variables.size() > 1) {
            throw new TypeException("TODO sizes don't match");

        } else if (variables.size() == 1) {
            if (!variables.get(0).getType().equals(expression.getType())) {
                throw new TypeException("TODO type mismatch");
            }
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Binary node) {
        Expression left = node.getLeft();
        Expression right = node.getRight();
        left.accept(this);
        right.accept(this);

        if (!left.getType().equals(right.getType())) {
            throw new TypeException("TODO left and right types don't match");

        } else {
            BinaryOperator binop = node.getOp();
            boolean isInteger = left.getType().equals(INT_TYPE);
            boolean isBool = left.getType().equals(BOOL_TYPE);

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

                if (left.getType() instanceof VariableType) {
                    VariableType leftType = (VariableType) left.getType();
                    node.setType(new VariableType(leftType.getPrimitiveType(), leftType.getNumBrackets()));

                } else {
                    throw new TypeException("must be VariableType");
                }

            } else {
                throw new TypeException("TODO invalid binary operator");
            }
        }
    }

    public void visit(BlockList node) {
        List<Block> blockList = node.getBlockList();
        for (Block block : blockList) {
            block.accept(this);
        }
        int size = blockList.size();
        if (size == 0) {
            node.setType(new VariableType(PrimitiveType.UNIT, 0));
        } else {
            Type lastType = blockList.get(size - 1).getType();
            node.setType(lastType);
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

        BlockList blockList = node.getBlockList();
        ReturnStatement returnStatement = node.getReturnStatement();

        blockList.accept(this);
        returnStatement.accept(this);

        // sanity check
        if (!blockList.getType().equals(UNIT_TYPE) || !returnStatement.getType().equals(VOID_TYPE)) {
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

        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);

            if (argument.getType() instanceof VariableType) {
                VariableType argType = (VariableType) argument.getType();
                argumentTypes.add(argType);
            } else {
                throw new TypeException("TODO: argument is not VariableType");
            }
        }

        Type type = context.get(node.getIdentifier());
        if (!(type instanceof FunctionType)) {
            throw new TypeException("TODO: this identifier doesn't point to a function");
        }

        FunctionType thisFuncType = (FunctionType) type;
        FunctionType funcType = new FunctionType(argumentTypes, thisFuncType.getReturnTypeList());
        if (!thisFuncType.equals(funcType)) {
            throw new TypeException("TODO: argument types do not match with function definition");
        }

        node.setType(thisFuncType.getReturnTypeList());
    }

    public void visit(FunctionDeclaration node) {
        // first pass takes care of identifier and functionType
        currentFunctionType = node.getFunctionType();
        List<Identifier> argList = node.getArgList();
        List<VariableType> argTypeList = currentFunctionType.getArgTypeList();
        // these are guaranteed to be the same size

        Context newContext = new Context(contexts.peek());
        for (int i = 0; i < argList.size(); i++) {
            newContext.put(argList.get(i), argTypeList.get(i));
        }

        contexts.push(newContext);
        node.getMethodBlock().accept(this);
        contexts.pop();

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

        Block trueBlock = node.getTrueBlock();
        trueBlock.accept(this);

        Optional<Block> falseBlock = node.getFalseBlock();
        if (falseBlock.isPresent()) {
            falseBlock.get().accept(this);
            PrimitiveType r1 = ((VariableType) trueBlock.getType()).getPrimitiveType();
            PrimitiveType r2 = ((VariableType) falseBlock.get().getType()).getPrimitiveType();
            node.setType(new VariableType(lub(r1,r2), 0));

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
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(ProcedureCall node) {
        Context context = contexts.peek();
        Identifier id = node.getIdentifier();
        if (!context.containsKey(id)) {
            throw new TypeException("TODO: procedure not defined");
        }

        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);

            if (argument.getType() instanceof VariableType) {
                VariableType argType = (VariableType) argument.getType();
                argumentTypes.add(argType);
            } else {
                throw new TypeException("TODO: argument is not VariableType");
            }
        }

        FunctionType funcType = new FunctionType(argumentTypes, new VariableTypeList(new ArrayList<>()));
        if (!funcType.equals(context.get(id))) {
            throw new TypeException("TODO: argument types do not match with procedure definition");
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Program node) {
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

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(ReturnStatement node) {
        // Checks if the return types match those outlined in the function declaration
        List<VariableType> functionReturnTypes = currentFunctionType
                                                 .getReturnTypeList()
                                                 .getVariableTypeList();
        List<Expression> returnExpressions = node.getValues();
        List<VariableType> returnTypes = new ArrayList<>();
        for (Expression expression : returnExpressions) {
            expression.accept(this);

            if (expression.getType() instanceof VariableType) {
                VariableType type = (VariableType) expression.getType();
                returnTypes.add(type);
            } else {
                throw new TypeException("TODO must be VariableType");
            }
        }

        if (functionReturnTypes.size() == 0 && returnTypes.size() != 0) {
            throw new TypeException("TODO procedure is returning something");
        }

        // precondition: they are the same length
        if(functionReturnTypes.size() != returnTypes.size()) {
            throw new TypeException("TODO return values do not match the number of types in function declaration");
        }

        for (int i = 0; i < returnTypes.size(); i++) {
            if (!functionReturnTypes.get(i).equals(returnTypes.get(i))) {
                throw new TypeException("TODO return values do not match types in function declaration");
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
        Expression expression = node.getExpression();
        expression.accept(this);

        UnaryOperator unop = node.getOp();
        if (expression.getType().equals(INT_TYPE) && unop == UnaryOperator.MINUS) {
            node.setType(new VariableType(PrimitiveType.INT, 0));

        } else if (expression.getType().equals(BOOL_TYPE) && unop == UnaryOperator.NOT) {
            node.setType(new VariableType(PrimitiveType.BOOL, 0));

        } else {
            throw new TypeException("TODO invalid unary operator");
        }
    }

    public void visit(Underscore node) {
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(UseStatement node) {
        String interfaceName = node.getIdentifier().getName();
        // If the interface name is the same as our source file, we need to
        // check that all its functions are implemented.
        if (interfaceName.equals(sourceFileName)) {
            String error = checkInterface(libPath, interfaceName, contexts.peek());
            if (error != null) {
                throw new TypeException(error);
            }
            node.setType(UNIT_TYPE);
            return;
        }
        String error = addInterface(libPath, interfaceName, contexts.peek());
        if (error != null) {
            throw new TypeException(error);
        }
        node.setType(UNIT_TYPE);
    }

    public void visit(WhileStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().equals(BOOL_TYPE)) {
            throw new TypeException("TODO");
        }

        Block block = node.getBlock();
        block.accept(this);

        if (!block.getType().equals(UNIT_TYPE) && !block.getType().equals(VOID_TYPE)) {
            throw new TypeException("TODO"); //debug
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    /**
     * Add function declarations in a given interface to a context.
     * @param libPath       a String of the path to the interface files
     * @param interfaceName a String of the name of the interface
     * @param context       a Context to add the function declarations to
     * @return              a String of the error or null if no error
     */
    public String addInterface(String libPath, String interfaceName, Context context) {
        List<FunctionDeclaration> declarations = new LinkedList<>();
        String error = InterfaceParser.parseInterface(libPath, interfaceName, declarations);
        for (FunctionDeclaration declaration : declarations) {
            context.put(declaration.getIdentifier(), declaration.getType());
        }
        return error;
    }

    /**
     * Checks that all declarations in an interface file are implemented in its associated in its xi file
     * @param libPath       a String of the path to the interface files
     * @param interfaceName a String of the name of the interface
     * @param context       a Context to add the function declarations to
     * @return              a String of the error or null if no error
     */
    public String checkInterface(String libPath, String interfaceName, Context context) {
        List<FunctionDeclaration> declarations = new LinkedList<>();
        String error = InterfaceParser.parseInterface(libPath, interfaceName, declarations);
        for (FunctionDeclaration declaration : declarations) {
            if (!context.get(declaration.getIdentifier()).equals(declaration.getType())) {
                return declaration.getIdentifier().getName() + " is not implemented";
            }
        }
        return error;
    }

    public PrimitiveType lub(PrimitiveType one, PrimitiveType two) {
        assert one == PrimitiveType.UNIT || one == PrimitiveType.VOID;
        assert two == PrimitiveType.UNIT || two == PrimitiveType.VOID;

        if (one == PrimitiveType.UNIT || two == PrimitiveType.UNIT) {
            return PrimitiveType.UNIT;
        } else {
            return PrimitiveType.VOID;
        }
    }
}