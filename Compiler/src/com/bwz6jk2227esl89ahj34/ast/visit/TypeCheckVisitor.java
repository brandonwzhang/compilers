package com.bwz6jk2227esl89ahj34.ast.visit;

import com.bwz6jk2227esl89ahj34.ast.*;
import com.bwz6jk2227esl89ahj34.ast.parse.InterfaceParser;
import com.bwz6jk2227esl89ahj34.ast.type.*;

import java.util.*;

public class TypeCheckVisitor implements NodeVisitor {
    // Path to interface files
    private String libPath;

    // Maintain a stack of contexts (identifier -> type map) for scoping
    private Stack<Context> contexts;

    private Context lastPoppedContext;

    // Local variable that stores current function in scope with a stack, used for return statements
    private FunctionType currentFunctionType;

    /**
     * Constructor for TypeCheckVisitor
     * @param libPath the directory for where to find the interface files.
     */
    public TypeCheckVisitor(String libPath) {
        contexts = new Stack<>();
        // initialize first context with length function, int[] -> int
        // when it should take bool[]. We handle this later below
        Context initContext = new Context();
        List<VariableType> lengthArgType = Collections.singletonList(ArrayType.intArray(1));
        VariableTypeList lengthReturnType =
                new VariableTypeList(Collections.singletonList(new IntType()));
        initContext.put(new Identifier("length"), new FunctionType(lengthArgType, lengthReturnType));
        contexts.push(initContext);

        this.libPath = libPath;
    }

    /**
     * Visits an ArrayIndex node. The arrayRef must type to a VariableType t with
     * at least one bracket, the index must type to int, and the overall type of
     * ArrayIndex is t with one less bracket.
     * @param node
     */
    public void visit(ArrayIndex node) {
        Expression arrayRef = node.getArrayRef();
        Expression index = node.getIndex();
        arrayRef.accept(this);
        index.accept(this);

        if (!index.getType().equals(new IntType())) {
            throw new TypeException("Index is not an integer", index.getRow(), index.getCol());
        }

        Type type = arrayRef.getType();
        // TODO: throw TypeException if not array
        VariableType arrayType = (ArrayType) type;
        if(arrayType.getNumBrackets() < 1) {
            throw new TypeException("Indexed element must be an array of at least dimension 1", arrayRef.getRow(), arrayRef.getCol());
        }
        node.setType(new VariableType(arrayType.getPrimitiveType(), arrayType.getNumBrackets() - 1));
    }

    /**
     * Visits an ArrayLiteral node. An empty array literal types to int[].
     * Otherwise, all elements of the ArrayLiteral must have the same type t as
     * the first element; then, the ArrayLiteral will have type t[].
     * @param node
     */
    public void visit(ArrayLiteral node) {
        // Empty array case ({})
        if (node.getValues().size() == 0) {
            node.setType(ArrayType.intArray(1));
            return;
        }

        // Check all types equal to the first type
        Type firstType = null;
        for (Expression e : node.getValues()) {
            e.accept(this);
            Type elementType = e.getType();

            // If element is a function call, it must return exactly one element
            // We extract that element's type and store it as a VariableType
            if (elementType instanceof VariableTypeList) {
                List<VariableType> returnTypes =
                        ((VariableTypeList) elementType).getVariableTypeList();
                int size = returnTypes.size();
                if (size == 0) {
                    throw new TypeException("Function calls inside " +
                            "array literals must return a value",
                            e.getRow(), e.getCol());

                } else if (size > 1) {
                    throw new TypeException("Function calls inside " +
                            "array literals cannot return more than one value",
                            e.getRow(), e.getCol());
                }
                // Extract the type from the VariableTypeList
                elementType = returnTypes.get(0);
            }

            if (firstType == null) {
                firstType = elementType;
            } else if (!firstType.equals(elementType)) {
                throw new TypeException("Types in the array literal " +
                        "must all match", e.getRow(), e.getCol());
            }
        }
        assert firstType != null;
        assert firstType instanceof VariableType;

        VariableType type = (VariableType) firstType;
        node.setType(new VariableType(type.getPrimitiveType(), type.getNumBrackets() + 1));
    }

    /**
     * Visits an Assignment node. The number of elements in the node's Variables
     * (LHS) must equal the number of elements on the RHS (VariableTypeList if
     * there's more than one). Then, each type on the LHS must be the same as
     * its respective element on the RHS. The Assignment has type Unit overall.
     * @param node
     */
    public void visit(Assignment node) {
        // Visit children
        List<Assignable> variables = node.getVariables();
        for (Assignable a : variables) {
            a.accept(this);
        }
        Expression expression = node.getExpression();
        expression.accept(this);
        assert !(expression.getType() instanceof FunctionType);

        // If LHS has more than one element, RHS must be VariableTypeList (func call)
        if (expression.getType() instanceof VariableTypeList) {
            VariableTypeList rhs = (VariableTypeList) expression.getType();
            if (variables.size() != rhs.getVariableTypeList().size()) {
                throw new TypeException("Assignment must have same number of elements on both sides", expression.getRow(), expression.getCol());
            }

            // Compare each LHS type with RHS type
            for (int i = 0; i < variables.size(); i++) {
                Assignable leftExpression = variables.get(i);

                Type leftType = leftExpression.getType();
                // If leftExpression is a TypedDeclaration, we need to get the
                // type of the variable that was declared, not unit.
                if (leftExpression instanceof TypedDeclaration) {
                    leftType = ((TypedDeclaration) leftExpression).getDeclarationType();
                }

                Type rightType = rhs.getVariableTypeList().get(i);

                assert leftType instanceof VariableType;

                if (!leftType.equals(new UnitType()) && !leftType.equals(rightType)) {
                    throw new TypeException("Type on left hand must match type on right hand", expression.getRow(), expression.getCol());
                }
            }

        } else if (variables.size() > 1) {
            throw new TypeException("Expression only evaluates to single value", expression.getRow(), expression.getCol());

        } else if (variables.size() == 1 &&
                !(variables.get(0) instanceof Underscore)) {
            Assignable leftExpression = variables.get(0);
            Type leftType = leftExpression.getType();
            // If leftExpression is a TypedDeclaration, we need to get the
            // type of the variable that was declared, not unit.
            if (leftExpression instanceof TypedDeclaration) {
                leftType = ((TypedDeclaration) leftExpression).getDeclarationType();
            }
            if (!leftType.equals(expression.getType())) {
                throw new TypeException("Types on LHS and RHS must match", expression.getRow(), expression.getCol());
            }
        } else if (variables.size() == 1 && variables.get(0) instanceof Underscore
                && !(expression instanceof FunctionCall)) {
            throw new TypeException("Expected Function Call", expression.getRow(), expression.getCol());
        }

        node.setType(new UnitType());
    }

    /**
     * Visits a Binary node. The left and right operands must be the same type
     * and check to the same type as the operand expects. Then the overall type
     * of the Binary will depend on the operand's return type.
     * @param node
     */
    public void visit(Binary node) {
        Expression left = node.getLeft();
        Expression right = node.getRight();
        left.accept(this);
        right.accept(this);

        // Extract single types if operands are function calls
        VariableType lefttype;
        VariableType righttype;
        if (left.getType() instanceof VariableTypeList) {
            List<VariableType> lefttypelist = ((VariableTypeList)left.getType()).getVariableTypeList();
            if(lefttypelist.size() != 1){
                throw new TypeException("Operand must return single value", left.getRow(), left.getCol());
            }
            lefttype = lefttypelist.get(0);
        }
        else {
            assert left.getType() instanceof VariableType;
            lefttype = (VariableType)left.getType();
        }
        if (right.getType() instanceof VariableTypeList) {
            List<VariableType> righttypelist = ((VariableTypeList)right.getType()).getVariableTypeList();
            if(righttypelist.size() != 1){
                throw new TypeException("Operand must be single value", right.getRow(), right.getCol());
            }
            righttype = righttypelist.get(0);
        }
        else {
            assert right.getType() instanceof VariableType;
            righttype = (VariableType)right.getType();
        }


        if (!lefttype.equals(righttype)) {
            throw new TypeException("Operands must be the same valid type", left.getRow(), left.getCol());
        }
        BinaryOperator binop = node.getOp();
        boolean isInteger = lefttype.equals(new IntType());
        boolean isBool = lefttype.equals(new IntType());

        boolean valid_int_binary_operator_int = BinarySymbol.INT_BINARY_OPERATOR_INT.contains(binop) && isInteger;
        boolean valid_int_binary_operator_bool = BinarySymbol.INT_BINARY_OPERATOR_BOOL.contains(binop) && isInteger;
        boolean valid_bool_binary_operator_bool = BinarySymbol.BOOL_BINARY_OPERATOR_BOOL.contains(binop) && isBool;

        if (valid_int_binary_operator_int) {
            node.setType(new IntType());
        } else if (valid_int_binary_operator_bool) {
            node.setType(new BoolType());
        } else if (valid_bool_binary_operator_bool) {
            node.setType(new BoolType());
        } else if (BinarySymbol.ARRAY_BINARY_OPERATOR_BOOL.contains(binop) && !isBool && !isInteger) {
            node.setType(new BoolType());
        } else if (binop.equals(BinaryOperator.PLUS) && !isBool && !isInteger) {
            assert lefttype instanceof VariableType;
            node.setType(lefttype);
        } else {
            throw new TypeException("Invalid binary operation", left.getRow(), left.getCol());
        }
    }

    /**
     * BlockList will typecheck if all of its blocks typecheck.
     * The type of BlockList is the type of its last block, or
     * unit if there are no blocks.
     * @param node
     */
    public void visit(BlockList node) {
        contexts.push(new Context(contexts.peek()));
        List<Block> blockList = node.getBlocks();
        for (Block block : blockList) {
            block.accept(this);
        }
        int size = blockList.size();
        if (size == 0) {
            node.setType(new UnitType());
        } else {
            Type lastType = blockList.get(size - 1).getType();
            node.setType(lastType);
        }
        lastPoppedContext = contexts.pop();
    }

    /**
     * BooleanLiteral has type bool.
     * @param node
     */
    public void visit(BooleanLiteral node) {
        node.setType(new BoolType());
    }

    /**
     * CharacterLiteral has type int.
     * @param node
     */
    public void visit(CharacterLiteral node) {
        node.setType(new IntType());
    }

    public void visit(ClassDeclaration node) {
        // TODO
    }

    /**
     * Checks that the given BlockList is guaranteed to return by the end
     * @param blockList
     * @return
     */
    public static boolean checkFunctionBlockList(BlockList blockList) {
        Block lastBlock = blockList.getBlocks().get(blockList.getBlocks().size() - 1);

        if (lastBlock instanceof ReturnStatement) {
            // Guaranteed to return at the end
            return true;
        } else if (lastBlock instanceof IfStatement) {
            // Allow for if {...return expr} else {...return expr}
            IfStatement ifStatement = (IfStatement) lastBlock;
            if (!ifStatement.getFalseBlock().isPresent()) {
                // We need a false block to guarantee return
                return false;
            }
            Block trueBlock = ifStatement.getTrueBlock();
            BlockList trueBlockList;
            if (trueBlock instanceof IfStatement) {
                // It's possible that an if statement will also guarantee a return
                trueBlockList = new BlockList(Collections.singletonList(trueBlock));
            } else if (trueBlock instanceof BlockList) {
                trueBlockList = (BlockList) trueBlock;
            } else {
                // We don't allow for single return statements not wrapped in
                // braces, so there cannot be a return here
                return false;
            }
            Block falseBlock = ifStatement.getFalseBlock().get();
            BlockList falseBlockList;
            if (falseBlock instanceof IfStatement) {
                // It's possible that an if statement will also guarantee a return
                falseBlockList = new BlockList(Collections.singletonList(falseBlock));
            } else if (falseBlock instanceof BlockList) {
                falseBlockList = (BlockList) falseBlock;
            } else {
                // We don't allow for single return statements not wrapped in
                // braces, so there cannot be a return here
                return false;
            }
            // Both branches must guarantee return
            return checkFunctionBlockList(trueBlockList) &&
                    checkFunctionBlockList(falseBlockList);
        }
        return false;
    }

    /**
     * Checks that top level return statements only occur at the end of the BlockList
     * @param blockList
     * @return
     */
    public static boolean checkProcedureBlockList(BlockList blockList) {
        List<Block> blocks = blockList.getBlocks();
        // Iterate until last element to make sure none of them are return statements
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (blocks.get(i) instanceof ReturnStatement) {
                return false;
            }
        }
        return true;
    }

    /**
     * The method called must exist in the context and the types of the
     * given arguments must match the argument types of the method.
     * The type of FunctionCall is the return type of the called method.
     * @param node
     */
    public void visit(FunctionCall node) {
        Context context = contexts.peek();
        Identifier id = node.getIdentifier();
        if (!context.containsKey(id)) {
            throw new TypeException("Function not defined", id.getRow(), id.getCol());
        }
        id.accept(this);

        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            argumentTypes.add(argType);
        }

        Type type = context.get(id);
        if (!(type instanceof FunctionType)) {
            throw new TypeException(id + " is not a function", id.getRow(), id.getCol());
        }

        FunctionType thisFuncType = (FunctionType) type;
        FunctionType funcType = new FunctionType(argumentTypes, thisFuncType.getReturnTypeList());
        // Function call don't match arguments AND
        // Not a valid length call...
        if (!thisFuncType.equals(funcType) && 
                !(id.getName().equals("length")
                && argumentTypes.size() == 1 && argumentTypes.get(0).getNumBrackets() > 0)) {
            throw new TypeException("Argument types do not match with function definition", id.getRow(), id.getCol());
        }

        if (thisFuncType.getReturnTypeList().getVariableTypeList().size() > 1) {
            node.setType(thisFuncType.getReturnTypeList());
        } else if (thisFuncType.getReturnTypeList().getVariableTypeList().size() == 0) {
            throw new TypeException("Procedure calls cannot be used as an expression", id.getRow(), id.getCol());
        } else {
            node.setType(thisFuncType.getReturnTypeList().getVariableTypeList().get(0));
        }
    }

    /**
     * This node's MethodBlock must typecheck in a context that includes the
     * arguments in the method signature.
     * FunctionDeclaration has type unit.
     * @param node
     */
    public void visit(FunctionDeclaration node) {
        currentFunctionType = node.getFunctionType();
        List<Identifier> argList = node.getArgumentIdentifiers();
        List<VariableType> argTypeList = currentFunctionType.getArgTypes();
        assert argList.size() == argTypeList.size();

        Context newContext = new Context(contexts.peek());
        for (int i = 0; i < argList.size(); i++) {
            newContext.put(argList.get(i), argTypeList.get(i));
        }

        BlockList blockList = node.getBlockList();

        contexts.push(newContext);
        blockList.accept(this);
        contexts.pop();

        if (node.getFunctionType().getReturnTypeList().getVariableTypeList().size() > 0) {
            // We have a function
            if (!checkFunctionBlockList(blockList)) {
                throw new TypeException("Function block does not guarantee return", node.getRow(), node.getCol());
            }
        } else {
            // We have a procedure
            if (!checkProcedureBlockList(blockList)) {
                throw new TypeException("Procedure block has unreachable code", node.getRow(), node.getCol());
            }
        }

        assert blockList.getType().equals(new UnitType()) || blockList.getType().equals(new VoidType());
        node.setType(new UnitType());
    }

    /**
     * The type of the Identifier should be in the context. If an Identifier
     * is visited and it is not already in the context, then it has not
     * been declared yet (type error).
     * @param node
     */
    public void visit(Identifier node) {
        // Check if identifier is in context
        Type type = contexts.peek().get(node);
        if (type == null) {
            throw new TypeException("Identifier does not exist in context", node.getRow(), node.getCol());
        }
        node.setType(type);
    }

    /**
     * The guard of an IfStatement must be a bool. If there is no false block,
     * then the type of IfStatement is unit. If there is a false block, the
     * type of IfStatement is lub(r1,r2) where r1 is the type of the true block
     * and r2 is the type of the false block.
     * @param node
     */
    public void visit(IfStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().equals(new BoolType())) {
            throw new TypeException("If statement guard must be of type bool", guard.getRow(), guard.getCol());
        }

        Block trueBlock = node.getTrueBlock();
        boolean trueBlockIsStatement = trueBlock instanceof Statement;
        // We need create and pop a new context if the block is just a statement
        if (trueBlockIsStatement) {
            if (trueBlock instanceof ReturnStatement) {
                throw new TypeException("Single return statement encountered in true block", trueBlock.getRow(), trueBlock.getCol());
            }
            contexts.push(new Context(contexts.peek()));
        }
        trueBlock.accept(this);
        if (trueBlockIsStatement) {
            contexts.pop();
        }

        Optional<Block> falseBlock = node.getFalseBlock();
        if (falseBlock.isPresent()) {
            boolean falseBlockIsStatement = falseBlock.get() instanceof Statement;
            // We need create and pop a new context if the block is just a statement
            if (falseBlockIsStatement) {
                if (falseBlock.get() instanceof ReturnStatement) {
                    throw new TypeException("Single return statement encountered in false block", falseBlock.get().getRow(), falseBlock.get().getCol());
                }
                contexts.push(new Context(contexts.peek()));
            }
            falseBlock.get().accept(this);
            if (falseBlockIsStatement) {
                contexts.pop();
            }
            Type r1 = trueBlock.getType();
            Type r2 = falseBlock.get().getType();
            // If has type void iff both blocks have type void
            node.setType(lub(r1, r2));
        } else {
            node.setType(new UnitType());
        }
    }

    /**
     * IntegerLiteral has type int.
     * @param node
     */
    public void visit(IntegerLiteral node) {
        node.setType(new IntType());
    }

    public void visit(MethodDeclaration node) {
        // TODO
    }

    public void visit(ObjectField node) {
        // TODO
    }

    public void visit(ObjectFunctionCall node) {
        // TODO
    }

    public void visit(ObjectInstantiation node) {
        // TODO
    }

    public void visit(ObjectProcedureCall node) {
        // TODO
    }

    /**
     * The method called must exist in the context and the types of the
     * given arguments must match the argument types of the method.
     * The method called cannot have a return type.
     * ProcedureCall has type unit.
     * @param node
     */
    public void visit(ProcedureCall node) {
        Context context = contexts.peek();
        Identifier id = node.getIdentifier();
        if (!context.containsKey(id)) {
            throw new TypeException("Procedure " + id.getName() + " not defined", id.getRow(), id.getCol());
        }

        List<VariableType> argumentTypes = ((FunctionType) context.get(id)).getArgTypes();
        List<Expression> passedArguments = node.getArguments();
        if (passedArguments.size() != argumentTypes.size()) {
            throw new TypeException("Too many arguments passed to " + id, id.getRow(), id.getCol());
        }
        for (int i = 0; i < passedArguments.size(); i++) {
            Expression argument = passedArguments.get(i);
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            if (!argType.equals(argumentTypes.get(i))) {
                throw new TypeException("Argument types do not match procedure definition", id.getRow(), id.getCol());
            }
        }

        FunctionType funcType = new FunctionType(argumentTypes, new VariableTypeList(new ArrayList<>()));
        if (!funcType.equals(context.get(id))) {
            throw new TypeException("You cannot call a method with a return " +
                    "value as a procedure.", id.getRow(), id.getCol());
        }

        node.setType(new UnitType());
    }

    /***
     * First, we sweep through the program and put all of the function
     * names in the context with its associated return type into the context
     * so that functions can use other functions.
     *
     * Then, it goes through the use statements to collect the methods
     * mentioned in the specified interface files.
     *
     * Then, all of the functions in the file are type checked.
     *
     * At any point when there is a conflict in the context, a TypeException
     * is thrown
     * @param node
     */
    public void visit(Program node) {
        // First pass collects all function types, adds to context
        for (FunctionDeclaration funcDec : node.getFunctionDeclarations()) {
            Identifier funcName = funcDec.getIdentifier();
            // Disallow overloading and shadowing
            if (contexts.peek().get(funcName) != null) {
                throw new TypeException(funcName.getName() + "declared multiple times",
                        funcName.getRow(), funcName.getCol());
            }
            FunctionType funcType = funcDec.getFunctionType();
            contexts.peek().put(funcName, funcType);
            //funcName.accept(this);
            funcName.setType(funcType);
        }

        // Add function declarations from interface files
        for (UseStatement useStatement : node.getUseBlock()) {
            useStatement.accept(this);
        }

        // Second pass typechecks all the function bodies
        for (FunctionDeclaration functionDeclaration : node.getFunctionDeclarations()) {
            functionDeclaration.accept(this);
        }

        node.setType(new UnitType());
    }

    /***
     * precondition: When we are at a return statement, currentFunctionType
     * will not be null (aka a return statement will only be inside functions,
     * which currentFunctionType keeps track of)
     * postcondition: If every expression being returned by the return statement
     * matches the currentFunctionType, then the type of the return statement
     * is set to VOID as it interrupts the program
     * @param node
     */
    public void visit(ReturnStatement node) {
        // Checks if the return types match those outlined in the function declaration
        List<VariableType> functionReturnTypes = currentFunctionType
                                                 .getReturnTypeList()
                                                 .getVariableTypeList();
        List<Expression> returnValues = node.getValues();
        // Check for correct number of return values
        if(functionReturnTypes.size() != returnValues.size()) {
            throw new TypeException("Number of return values does not match function declaration",
                    node.getRow(), node.getCol());
        }

        for (int i = 0; i < returnValues.size(); i++) {
            Expression expression = returnValues.get(i);
            expression.accept(this);
            // Expression must have a VariableType
            assert expression.getType() instanceof VariableType;
            VariableType type = (VariableType) expression.getType();
            if (!functionReturnTypes.get(i).equals(type)) {
                throw new TypeException("Return values do not match types in function declaration",
                        expression.getRow(), expression.getCol());
            }
        }
        node.setType(new VoidType());
    }

    /***
     * A String literal in Xi will always have the type of an int array
     * @param node
     */
    public void visit(StringLiteral node) {
        node.setType(new ArrayType(new IntType(), 1));
    }

    /***
     * A TypedDeclaration is a declaration that initializes the variable
     * and declares it a certain type
     * If that variable is already defined in the context, a TypeException
     * will be thrown
     * @param node
     */
    public void visit(TypedDeclaration node) {
        Identifier identifier = node.getIdentifier();
        Context context = contexts.peek();
        if (context.containsKey(identifier)) {
            throw new TypeException("Variable " + identifier.getName() +
                    " already declared in scope", node.getRow(), node.getCol());
        }
        context.put(identifier, node.getDeclarationType());
        identifier.accept(this);
        for (Expression expression : node.getArraySizes()) {
            expression.accept(this);
        }
        node.setType(new UnitType());
    }

    /***
     * A unary node that is wrapping either a negative number or a negation
     * of a boolean
     * for a negative number, if the expression wrapped in the unary node is an
     * int, then the unary node will have type int
     * for a negation of a boolean, if the expression wrapped in the unary
     * node is in a bool, then the unary node will have type bool
     * @param node
     */
    public void visit(Unary node) {
        Expression expression = node.getExpression();
        expression.accept(this);

        UnaryOperator unop = node.getOp();
        if (expression.getType().isInt() && unop ==
                UnaryOperator.MINUS) {
            node.setType(new IntType());
        } else if (expression.getType().isBool() && unop ==
                UnaryOperator.NOT) {
            node.setType(new BoolType());
        } else {
            throw new TypeException("Invalid unary operator",
                    node.getRow(), node.getCol());
        }
    }

    /**
     * A node of type underscore will always be set to type UNIT
     * @param node
     */
    public void visit(Underscore node) {
        node.setType(new UnitType());
    }

    /***
     * When we encounter a node that represents a use statement
     * we invoke the addInterface method to properly add all of the
     * methods in the specified ixi file
     * if there is an error in the ixi file then a type exception is thrown
     * otherwise, the type of the use statement is set to type unit
     * @param node
     */
    public void visit(UseStatement node) {
        String interfaceName = node.getIdentifier().getName();
        String error = addInterface(libPath, interfaceName, contexts.peek());
        if (error != null) {
            throw new TypeException(error, node.getRow(), node.getCol());
        }
        node.setType(new UnitType());
    }

    /**
     * If the expression in the guard is a bool
     * and the statement is a type R that produces a new context
     * then the while statement type-checks as a valid while statement
     * that is of type UNIT with the same environment prior to entering the
     * while statement
     * @param node
     */
    public void visit(WhileStatement node) {
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().isBool()) {
            throw new TypeException("While statement guard must be of type bool",
                    guard.getRow(), guard.getCol());
        }

        Block block = node.getBlock();
        boolean blockIsStatement = block instanceof Statement;
        // We need create and pop a new context if the block is just a statement
        if (blockIsStatement) {
            contexts.push(new Context(contexts.peek()));
        }
        block.accept(this);
        // Blocks should only have either unit or void type
        assert block.getType().isUnit() ||
                block.getType().isVoid();
        if (blockIsStatement) {
            // Pop off the context for the statement
            contexts.pop();
        }

        node.setType(new UnitType());
    }

    /**
     * Add function declarations in a given interface to a context.
     * @param libPath       a String of the path to the interface files
     * @param interfaceName a String of the name of the interface
     * @param context       a Context to add the function declarations to
     * @return              a String of the error or null if no error
     */
    public String addInterface(String libPath, String interfaceName,
                               Context context) {
        List<FunctionDeclaration> declarations = new LinkedList<>();
        String error = InterfaceParser.parseInterface(libPath,
                interfaceName, declarations);
        for (FunctionDeclaration declaration : declarations) {
            // Error if function already exists in context with different type
            FunctionType existingDeclarationType =
                    (FunctionType) context.get(declaration.getIdentifier());
            if (existingDeclarationType != null) {
                if (!existingDeclarationType.equals(declaration.getFunctionType())) {
                    return declaration.getIdentifier().getName() +
                            " already declared with different type";
                }
                continue;
            }

            // Add the declaration to the context otherwise
            context.put(declaration.getIdentifier(), declaration.getFunctionType());
        }
        return error;
    }

    /**
     * precondition: one and two are both either UNIT or VOID
     * postcondition: if either one or two are UNIT then UNIT will be returned
     * else VOID is returned
     *
     * @param one the first input to the lub function (UNIT or VOID)
     * @param two the second input to the lub function (UNIT or VOID)
     * @return the output of the function lub as specified in the Xi Type Spec
     *
     */
    public Type lub(Type one, Type two) {
        assert one.isUnit() || one.isVoid();
        assert two.isUnit() || two.isVoid();

        if (one.isUnit() || two.isVoid()) {
            return new UnitType();
        } else {
            return new VoidType();
        }
    }
}