package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.*;

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
            throw new TypeException("Index is not an integer", node.getRow(), node.getCol());
        }

        Type type = node.getArrayRef().getType();
        assert type instanceof VariableType;
        VariableType arrayType = (VariableType) type;
        if(arrayType.getNumBrackets() < 1) {
            throw new TypeException("Indexed element must be an array of at least dimension 1", node.getRow(), node.getCol());
        }
        node.setType(new VariableType(arrayType.getPrimitiveType(), arrayType.getNumBrackets() - 1));
    }

    public void visit(ArrayLiteral node) {
        // Empty array case ({})
        if (node.getValues().size() == 0) {
            node.setType(new VariableType(PrimitiveType.INT, 1));
            return;
        }

        // Check all types equal to the first type
        Type firstType = null;
        for (Expression e : node.getValues()) {
            e.accept(this);
            Type elementType = e.getType();
            if (elementType instanceof VariableTypeList) {
                List<VariableType> returnTypes =
                        ((VariableTypeList) elementType).getVariableTypeList();
                int size = returnTypes.size();
                if (size == 0) {
                    throw new TypeException("Function calls inside " +
                            "array literals must return a value",
                            node.getRow(), node.getCol());

                } else if (size > 1) {
                    throw new TypeException("Function calls inside " +
                            "array literals cannot return more than one value",
                            node.getRow(), node.getCol());
                }
                elementType = returnTypes.get(0);
            }

            if (firstType == null) {
                firstType = elementType;
            } else if (!firstType.equals(elementType)) {
                throw new TypeException("Types in the array literal " +
                        "must all match", node.getRow(), node.getCol());
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
        assert !(expression.getType() instanceof FunctionType);

        // If LHS has more than one element, RHS must be VariableTypeList
        if (expression.getType() instanceof VariableTypeList) {
            VariableTypeList rhs = (VariableTypeList) expression.getType();
            if (variables.size() != rhs.getVariableTypeList().size()) {
                throw new TypeException("Assignment must have same number of elements on both sides", node.getRow(), node.getCol());
            }

            for (int i = 0; i < variables.size(); i++) {
                Type leftType = variables.get(i).getType();
                Type rightType = rhs.getVariableTypeList().get(i);

                assert leftType instanceof VariableType;

                if (!leftType.equals(UNIT_TYPE) && !leftType.equals(rightType)) {
                    throw new TypeException("Type on left hand must match type on right hand", node.getRow(), node.getCol());
                }
            }

        } else if (variables.size() > 1) {
            throw new TypeException("Too many elements on LHS of Assignment", node.getRow(), node.getCol());

        } else if (variables.size() == 1) {
            if (!variables.get(0).getType().equals(expression.getType())) {
                throw new TypeException("Types on LHS and RHS must match", node.getRow(), node.getCol());
            }
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Binary node) {
        Expression left = node.getLeft();
        Expression right = node.getRight();
        left.accept(this);
        right.accept(this);

        // precondition: left and right only check to a VariableType
        if (!left.getType().equals(right.getType())) {
            throw new TypeException("Operands must be the same valid type", node.getRow(), node.getCol());

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
                assert left.getType() instanceof VariableType;
                VariableType leftType = (VariableType) left.getType();
                node.setType(new VariableType(leftType.getPrimitiveType(), leftType.getNumBrackets()));
            } else {
                throw new TypeException("Invalid binary operator", node.getRow(), node.getCol());
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
        assert blockList.getType().equals(UNIT_TYPE) || returnStatement.getType().equals(VOID_TYPE);

        node.setType(new VariableType(PrimitiveType.UNIT, 0));

        contexts.pop();
    }

    public void visit(FunctionCall node) {
        Context context = contexts.peek();
        if (!context.containsKey(node.getIdentifier())) {
            throw new TypeException("Function not defined", node.getRow(), node.getCol());
        }

        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            argumentTypes.add(argType);
        }

        Type type = context.get(node.getIdentifier());
        if (!(type instanceof FunctionType)) {
            throw new TypeException("This identifier doesn't point to a function", node.getRow(), node.getCol());
        }

        FunctionType thisFuncType = (FunctionType) type;
        FunctionType funcType = new FunctionType(argumentTypes, thisFuncType.getReturnTypeList());
        //function call don't match arguments AND
        //not a valid length call...
        if (!thisFuncType.equals(funcType) && 
                !(node.getIdentifier().getName().equals("length")
                && argumentTypes.size() == 1 && argumentTypes.get(0).getNumBrackets() > 0)) {
            throw new TypeException("Argument types do not match with function definition", node.getRow(), node.getCol());
        }

        node.setType(thisFuncType.getReturnTypeList());
    }

    public void visit(FunctionDeclaration node) {
        currentFunctionType = node.getFunctionType();
        List<Identifier> argList = node.getArgList();
        List<VariableType> argTypeList = currentFunctionType.getArgTypeList();
        assert argList.size() == argTypeList.size();

        Context newContext = new Context(contexts.peek());
        for (int i = 0; i < argList.size(); i++) {
            newContext.put(argList.get(i), argTypeList.get(i));
        }

        MethodBlock methodBlock = node.getMethodBlock();

        contexts.push(newContext);
        methodBlock.accept(this);
        contexts.pop();

        assert(methodBlock.getType() == UNIT_TYPE || methodBlock.getType() == VOID_TYPE);
        node.setType(UNIT_TYPE);
    }

    public void visit(Identifier node) {
        // Check if identifier is in context
        Type type = contexts.peek().get(node);
        if (type == null) {
            throw new TypeException("Identifier does not exist in context", node.getRow(), node.getCol());
        }
        node.setType(type);
    }

    public void visit(IfStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().equals(BOOL_TYPE)) {
            throw new TypeException("If statement guard must be of type bool", node.getRow(), node.getCol());
        }

        Block trueBlock = node.getTrueBlock();
        trueBlock.accept(this);

        Optional<Block> falseBlock = node.getFalseBlock();
        if (falseBlock.isPresent()) {
            falseBlock.get().accept(this);
            PrimitiveType r1 = ((VariableType) trueBlock.getType()).getPrimitiveType();
            PrimitiveType r2 = ((VariableType) falseBlock.get().getType()).getPrimitiveType();
            // If has type void iff both blocks have type void
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
            throw new TypeException("Procedure " + id.getName() + " not defined", node.getRow(), node.getCol());
        }

        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            argumentTypes.add(argType);
        }

        FunctionType funcType = new FunctionType(argumentTypes, new VariableTypeList(new ArrayList<>()));
        if (!funcType.equals(context.get(id))) {
            throw new TypeException("Argument types do not match procedure definition", node.getRow(), node.getCol());
        }

        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(Program node) {
        // First pass collects all function types, adds to context
        for (FunctionDeclaration funcDec : node.getFuncDecs()) {
            Identifier funcName = funcDec.getIdentifier();
            // Disallow overloading and shadowing
            if (contexts.peek().get(funcName) != null) {
                throw new TypeException(funcName.getName() + "declared multiple times", node.getRow(), node.getCol());
            }
            FunctionType funcType = funcDec.getFunctionType();
            contexts.peek().put(funcName, funcType);
        }

        // Add function declarations from interface files
        for (UseStatement useStatement : node.getUseBlock()) {
            useStatement.accept(this);
        }

        // Second pass typechecks all the function bodies
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
        List<VariableType> returnTypes = new ArrayList<>();

        for (Expression expression : node.getValues()) {
            expression.accept(this);
            // Expression must have a VariableType
            assert expression.getType() instanceof VariableType;
            VariableType type = (VariableType) expression.getType();
            returnTypes.add(type);
        }

        // Precondition: they are the same length
        if(functionReturnTypes.size() != returnTypes.size()) {
            throw new TypeException("Number of return values does not match function declaration", node.getRow(), node.getCol());
        }

        for (int i = 0; i < returnTypes.size(); i++) {
            if (!functionReturnTypes.get(i).equals(returnTypes.get(i))) {
                throw new TypeException("Return values do not match types in function declaration", node.getRow(), node.getCol());
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
            throw new TypeException("Variable " + identifier.getName() + " already declared in scope",
                    node.getRow(), node.getCol());
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
            throw new TypeException("Invalid unary operator", node.getRow(), node.getCol());
        }
    }

    public void visit(Underscore node) {
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(UseStatement node) {
        String interfaceName = node.getIdentifier().getName();
        String error = addInterface(libPath, interfaceName, contexts.peek());
        if (error != null) {
            throw new TypeException(error, node.getRow(), node.getCol());
        }
        node.setType(new VariableType(PrimitiveType.UNIT, 0));
    }

    public void visit(WhileStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().equals(BOOL_TYPE)) {
            throw new TypeException("While statement guard must be of type bool", node.getRow(), node.getCol());
        }

        Block block = node.getBlock();
        block.accept(this);

        // Blocks should only have either unit or void type
        assert block.getType().equals(UNIT_TYPE) || block.getType().equals(VOID_TYPE);
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
            // Error if function already exists in context with different type
            FunctionType existingDeclarationType =
                    (FunctionType) context.get(declaration.getIdentifier());
            if (existingDeclarationType != null) {
                if (existingDeclarationType.equals(declaration.getFunctionType())) {
                    return declaration.getIdentifier().getName() +
                            " already declared with different type";
                }
            }

            // Add the declaration to the context otherwise
            context.put(declaration.getIdentifier(), declaration.getFunctionType());
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