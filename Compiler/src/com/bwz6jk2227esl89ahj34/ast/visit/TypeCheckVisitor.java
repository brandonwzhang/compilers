package com.bwz6jk2227esl89ahj34.ast.visit;

import com.bwz6jk2227esl89ahj34.ast.*;
import com.bwz6jk2227esl89ahj34.ast.parse.Interface;
import com.bwz6jk2227esl89ahj34.ast.parse.InterfaceParser;
import com.bwz6jk2227esl89ahj34.ast.type.*;

import java.util.*;

public class TypeCheckVisitor implements NodeVisitor {
    // Path to interface files
    private String libPath;
    // Name of the current file
    private String moduleName;

    // Maintain a stack of contexts (identifier -> type map) for scoping
    private Stack<Context> contexts = new Stack<>();

    private Map<Identifier, ClassDeclaration> classes = new HashMap<>();

    // Type of the function we're currently type checking
    private FunctionType currentFunctionType;
    // Type of current class we're type checking
    private Optional<ClassType> currentClassType = Optional.empty();

    // Keep a set of parsed interfaces so we don't double-parse
    private Set<String> parsedInterfaces = new HashSet<>();

    /**
     * Constructor for TypeCheckVisitor
     * @param libPath the directory for where to find the interface files.
     */
    public TypeCheckVisitor(String libPath, String fileName) {
        // initialize first context with length function, int[] -> int
        // when it should take bool[]. We handle this later below
        Context initContext = new Context();
        List<VariableType> lengthArgType = Collections.singletonList(new ArrayType(new IntType(), 1));
        VariableTypeList lengthReturnType =
                new VariableTypeList(Collections.singletonList(new IntType()));
        initContext.put(new Identifier("length"), new FunctionType(lengthArgType, lengthReturnType));
        contexts.push(initContext);

        this.libPath = libPath;
        this.moduleName = fileName;
    }

    /**
     * Check if a given type is a valid function type
     */
    private boolean isValidFunctionType(Type type) {
        if (!(type instanceof FunctionType)) {
            return false;
        }
        // check if all args are valid variableTypes
        FunctionType functionType = (FunctionType) type;
        for (VariableType variableType : functionType.getArgTypes()) {
            if (!isValidVariableType(variableType)) {
                return false;
            }
        }
        // check if all return types are valid variableTypes
        for (VariableType variableType : functionType.getReturnTypeList().getVariableTypeList()) {
            if (!isValidVariableType(variableType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a given type is either an int, bool, unit, void or a class
     * that has been declared
     */
    private boolean isValidVariableType(Type type) {
        if (!(type instanceof VariableType)) {
            return false;
        }

        if (!(type instanceof ClassType || type instanceof ArrayType)) {
            return true;
        }
        ClassType classType;
        if (type instanceof ClassType) {
            classType = (ClassType) type;
        } else {
            ArrayType arrayType = (ArrayType) type;
            if (!(arrayType.getBaseType() instanceof ClassType)) {
                return true;
            }
            classType = (ClassType) arrayType.getBaseType();
        }
        if (!classes.containsKey(classType.getIdentifier())) {
            return false;
        }
        return true;
    }

    /**
     * Visits an ArrayIndex node. The arrayRef must type to a VariableType t with
     * at least one bracket, the index must type to int, and the overall type of
     * ArrayIndex is t with one less bracket.
     * @param node
     */
    public void visit(ArrayIndex node) {
        // visit subExpressions
        Expression arrayRef = node.getArrayRef();
        Expression index = node.getIndex();
        arrayRef.accept(this);
        index.accept(this);

        if (!index.getType().isInt()) {
            throw new TypeException("Index is not an integer", index.getRow(), index.getCol());
        }

        Type type = arrayRef.getType();
        if (!(type instanceof ArrayType)) {
            throw new TypeException("Indexed element must be an array of at least dimension 1",
                    arrayRef.getRow(), arrayRef.getCol());
        }
        ArrayType arrayType = (ArrayType) type;
        int newNumBrackets = arrayType.getNumBrackets() - 1;
        if (newNumBrackets == 0) {
            node.setType(arrayType.getBaseType());
            return;
        }
        node.setType(new ArrayType(arrayType.getBaseType(), newNumBrackets));
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
            node.setType(new ArrayType(new IntType(), 1));
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
        if (type instanceof ArrayType) {
            // If we have an array of arrays, we increment numBrackets by 1
            ArrayType arrayType = (ArrayType) type;
            node.setType(new ArrayType(arrayType.getBaseType(), arrayType.getNumBrackets() + 1));
            return;
        }
        node.setType(new ArrayType((BaseType) type, 1));
    }

    /**
     * Checks if type is a subtype of superType
     */
    private boolean isSubtype(Type type, Type superType) {
        // Unit is a supertype of everything
        if (superType instanceof UnitType) {
            return true;
        }
        // Reflexivity of subtype
        if (type.equals(superType)) {
            return true;
        }
        // Null is a subtype of everything but int and bool
        if (type.equals(new NullType()) && !(superType.isBool() || superType.isInt())) {
            return true;
        }
        if (!(type instanceof ClassType && superType instanceof ClassType)) {
            // Only a class can be a subtype of another class
            return false;
        }
        // At this point we need to check if type is a strict subtype of superType
        ClassType classType = (ClassType) type;
        ClassType superClassType = (ClassType) superType;
        ClassDeclaration classDecl = classes.get(classType.getIdentifier());
        ClassDeclaration superClassDecl = classes.get(superClassType.getIdentifier());

        // Check if any ancestors of type equals superType
        while (classDecl.getParentIdentifier().isPresent()) {
            Identifier parentIdentifier = classDecl.getParentIdentifier().get();
            classDecl = classes.get(parentIdentifier);
            if (classDecl.getIdentifier().equals(superClassDecl.getIdentifier())) {
                return true;
            }
        }
        return false;
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

                assert leftType instanceof VariableType || leftType.equals(new UnitType());

                if (!isSubtype(rightType, leftType)) {
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
            if (!isSubtype(expression.getType(), leftType)) {
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

        /*
            For == and !=, both sides can be nullable (arraytype, classtype, nulltype),
            but we can't have only one be nullable.
         */
        BinaryOperator binop = node.getOp();
        if (binop == BinaryOperator.EQUAL || binop == BinaryOperator.NOT_EQUAL) {
            if (lefttype.isNullable()) {
                if (righttype.isNullable()) {
                    node.setType(new BoolType());
                }
                else {
                    throw new TypeException("Both operands for == or != need to be Objects or Null", left.getRow(), left.getCol());
                }
            }
        }

        // Check that left and right types are cooperative
        if (!lefttype.equals(righttype)) {
            throw new TypeException("Operands must be the same valid type", left.getRow(), left.getCol());
        }
        boolean isInteger = lefttype.isInt();
        boolean isBool = lefttype.isBool();

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
        // create new context (copy of current)
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
        contexts.pop();
    }

    /**
     * BooleanLiteral has type bool.
     * @param node
     */
    public void visit(BooleanLiteral node) {
        node.setType(new BoolType());
    }

    // TODO: check if it occurs inside loop?
    public void visit(Break node) {

    }

    /**
     * CharacterLiteral has type int.
     * @param node
     */
    public void visit(CharacterLiteral node) {
        node.setType(new IntType());
    }

    /**
     * Returns the type of the field in a given class. Returns null if field
     * cannot be found.
     */
    private VariableType getFieldType(Identifier fieldName, ClassDeclaration cd) {
        for (TypedDeclaration td : cd.getFields()) {
            if (td.getIdentifier().equals(fieldName)) {
                return td.getDeclarationType();
            }
        }
        // Since all fields are private, if we don't find it in this class, we
        // cannot look in parent classes
        return null;
    }

    /**
     * Returns the type of the method in a given class. Returns null if method
     * cannot be found.
     */
    private FunctionType getMethodType(Identifier methodName, ClassDeclaration cd) {
        assert cd != null;
        for (MethodDeclaration md : cd.getMethods()) {
            if (md.getFunctionDeclaration().getIdentifier().equals(methodName)) {
                return md.getFunctionDeclaration().getFunctionType();
            }
        }
        // At this point the method was not found in this class, so we look in
        // parent classes
        if (!cd.getParentIdentifier().isPresent()) {
            // There is no parent, so this method does not exist
            return null;
        }
        return getMethodType(methodName, classes.get(cd.getParentIdentifier().get()));
    }

    public void visit(ClassDeclaration node) {
        contexts.push(new Context(contexts.peek()));
        currentClassType = Optional.of(new ClassType(node.getIdentifier()));
        // Check if any fields are incorrectly shadowed
        for (TypedDeclaration td : node.getFields()) {
            // Accept the typed declaration to add it to the context
            td.accept(this);
        }

        // Check if any methods are overridden incorrectly
        Set<Identifier> seenMethods = new HashSet<>();
        for (MethodDeclaration md : node.getMethods()) {
            FunctionDeclaration fd = md.getFunctionDeclaration();
            // Make sure methods aren't declared multiple times in same class
            if (seenMethods.contains(fd.getIdentifier())) {
                throw new TypeException("Method " + fd.getIdentifier().getName() + " declared multiple times in class",
                        fd.getRow(), fd.getCol());
            }
            seenMethods.add(fd.getIdentifier());
            if (node.getParentIdentifier().isPresent()) {
                Identifier parentName = node.getParentIdentifier().get();
                ClassDeclaration parentClass = classes.get(parentName);
                FunctionType parentMethodType = getMethodType(fd.getIdentifier(), parentClass);
                // If parent contains the method, we have the make sure types match
                if (parentMethodType != null) {
                    if (!fd.getFunctionType().equals(parentMethodType)) {
                        throw new TypeException("Type of method " + fd.getIdentifier().getName() + " does not match parent",
                                fd.getRow(), fd.getCol());
                    }
                }
            }

            // Check that the method block is well typed and the function type is a valid type
            md.accept(this);
        }
        node.setType(new UnitType());
        currentClassType = Optional.empty();
        contexts.pop();
    }

    /**
     * Checks that the given BlockList is guaranteed to return by the end
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
        // Check that the function exists in the context
        Identifier id = node.getIdentifier();
        if (!context.containsKey(id)) {
            throw new TypeException("Function not defined", id.getRow(), id.getCol());
        }
        id.accept(this);

        // Gather argument types
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

        FunctionType funcType = (FunctionType) type;
        // Function call don't match arguments AND

        // Check call arguments to make sure they match
        // We need to handle length calls as a special case since they can take multiple types
        if (id.getName().equals("length")) {
            if (!(argumentTypes.size() == 1 && argumentTypes.get(0) instanceof ArrayType)) {
                throw new TypeException("Argument types do not match with function definition", id.getRow(), id.getCol());
            }
        } else {
            // Check that the correct number of arguments are passed
            if (argumentTypes.size() != funcType.getArgTypes().size()) {
                throw new TypeException("Too many arguments passed to " + node.getIdentifier().getName(),
                        node.getRow(), node.getCol());
            }
            for (int i = 0; i < argumentTypes.size(); i++) {
                VariableType callArgumentType = argumentTypes.get(i);
                VariableType argumentType = funcType.getArgTypes().get(i);
                if (!isSubtype(callArgumentType, argumentType)) {
                    throw new TypeException("Argument types do not match with function definition",
                            id.getRow(), id.getCol());
                }
            }
        }

        if (funcType.getReturnTypeList().getVariableTypeList().size() > 1) {
            node.setType(funcType.getReturnTypeList());
        } else if (funcType.getReturnTypeList().getVariableTypeList().size() == 0) {
            throw new TypeException("Procedure calls cannot be used as an expression", id.getRow(), id.getCol());
        } else {
            node.setType(funcType.getReturnTypeList().getVariableTypeList().get(0));
        }
    }

    /**
     * This node's MethodBlock must typecheck in a context that includes the
     * arguments in the method signature.
     * FunctionDeclaration has type unit.
     * @param node
     */
    public void visit(FunctionDeclaration node) {
        if (!isValidFunctionType(node.getFunctionType())) {
            throw new TypeException("Invalid function type", node.getRow(), node.getCol());
        }
        currentFunctionType = node.getFunctionType();
        List<Identifier> argList = node.getArgumentIdentifiers();
        List<VariableType> argTypeList = currentFunctionType.getArgTypes();
        assert argList.size() == argTypeList.size();

        // create new context containing arguments
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
        // "this" variable always has the type of the class it's used in
        // If "this" is used outside of a class, we throw an exception
        if (node.getName().equals("this")) {
            if (!currentClassType.isPresent()) {
                throw new TypeException("'this' cannot be used outside of a class declaration",
                        node.getRow(), node.getCol());
            }
            node.setType(currentClassType.get());
            return;
        }
        // Check if identifier is in context
        Type type = contexts.peek().get(node);
        if (type == null) {
            throw new TypeException("Identifier " + node.getName() + " does not exist in context",
                    node.getRow(), node.getCol());
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
        // check guard
        Expression guard = node.getGuard();
        guard.accept(this);
        if (!guard.getType().isBool()) {
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
                    throw new TypeException("Single return statement encountered in false block",
                            falseBlock.get().getRow(), falseBlock.get().getCol());
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

    /**
     * MethodDeclaration has the type of its wrapped FunctionDeclaration
     */
    public void visit(MethodDeclaration node) {
        // Check that the function declaration wrapped in this method is typed
        FunctionDeclaration fd = node.getFunctionDeclaration();

        // Make sure no argument names clash with class fields
        Identifier className = node.getClassIdentifier();
        assert classes.containsKey(className);
        ClassDeclaration cd = classes.get(className);
        List<Identifier> args = fd.getArgumentIdentifiers();
        for (TypedDeclaration td : cd.getFields()) {
            if (args.contains(td.getIdentifier())) {
                throw new TypeException("Method arguments may not shadow class fields", node.getRow(), node.getCol());
            }
        }

        fd.accept(this);
        node.setType(fd.getType());
    }

    /**
     * Null always has NullType
     */
    public void visit(Null node) {
        node.setType(new NullType());
    }

    /**
     * The type of an object's field is specified in the object's ClassDeclaration
     */
    public void visit(ObjectField node) {
        node.getObject().accept(this);
        assert node.getObject().getType() instanceof ClassType;
        ClassType classType = (ClassType) node.getObject().getType();
        // We can only access private fields from methods in the class
        if (!currentClassType.isPresent() || !currentClassType.get().equals(classType)) {
            throw new TypeException("Cannot access private field", node.getRow(), node.getCol());
        }
        Identifier className = classType.getIdentifier();
        ClassDeclaration cd = classes.get(className);
        Identifier fieldName = node.getField();
        // Get the type for this field
        VariableType type = getFieldType(fieldName, cd);
        if (type == null) {
            throw new TypeException("Class " + className.getName() + " does not contain field " + fieldName.getName(),
                    fieldName.getRow(), fieldName.getCol());
        }
        node.setType(type);
    }

    /**
     * The dispatch vector is used to find the location of the method
     */
    public void visit(ObjectFunctionCall node) {
        node.getObject().accept(this);
        assert node.getObject().getType() instanceof ClassType;
        Identifier className = ((ClassType) node.getObject().getType()).getIdentifier();
        ClassDeclaration cd = classes.get(className);

        // Extract the types for tha passed arguments
        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            argumentTypes.add(argType);
        }

        Identifier methodName = node.getIdentifier();
        FunctionType funcType = getMethodType(methodName, cd);
        if (funcType == null) {
            throw new TypeException("Class " + className.getName() + " does not contain method " + methodName.getName(),
                    methodName.getRow(), methodName.getCol());
        }

        // Check that the correct number of arguments are passed
        if (argumentTypes.size() != funcType.getArgTypes().size()) {
            throw new TypeException("Too many arguments passed to " + node.getIdentifier().getName(),
                    node.getRow(), node.getCol());
        }

        // Check call arguments to make sure they match
        for (int i = 0; i < argumentTypes.size(); i++) {
            VariableType callArgumentType = argumentTypes.get(i);
            VariableType argumentType = funcType.getArgTypes().get(i);
            if (!isSubtype(callArgumentType, argumentType)) {
                throw new TypeException("Argument types do not match with function definition",
                        methodName.getRow(), methodName.getCol());
            }
        }

        if (funcType.getReturnTypeList().getVariableTypeList().size() > 1) {
            node.setType(funcType.getReturnTypeList());
        } else if (funcType.getReturnTypeList().getVariableTypeList().size() == 0) {
            throw new TypeException("Procedure calls cannot be used as an expression",
                    methodName.getRow(), methodName.getCol());
        } else {
            node.setType(funcType.getReturnTypeList().getVariableTypeList().get(0));
        }
    }

    /**
     * Has the type of the class that was specified
     */
    public void visit(ObjectInstantiation node) {
        // Set the type to the class
        node.setType(new ClassType(node.getClassIdentifier()));
    }

    /**
     * Dispatch vector is used to find location of the method
     */
    public void visit(ObjectProcedureCall node) {
        node.getObject().accept(this);
        assert node.getObject().getType() instanceof ClassType;
        Identifier className = ((ClassType) node.getObject().getType()).getIdentifier();
        ClassDeclaration cd = classes.get(className);

        // Extract the types for tha passed arguments
        List<VariableType> argumentTypes = new ArrayList<>();
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            argumentTypes.add(argType);
        }

        Identifier methodName = node.getIdentifier();
        FunctionType funcType = getMethodType(methodName, cd);
        if (funcType == null) {
            throw new TypeException("Class " + className.getName() + " does not contain method " + methodName.getName(),
                    methodName.getRow(), methodName.getCol());
        }

        // Check that the correct number of arguments are passed
        if (argumentTypes.size() != funcType.getArgTypes().size()) {
            throw new TypeException("Too many arguments passed to " + node.getIdentifier().getName(),
                    node.getRow(), node.getCol());
        }

        // Check call arguments to make sure they match
        for (int i = 0; i < argumentTypes.size(); i++) {
            VariableType callArgumentType = argumentTypes.get(i);
            VariableType argumentType = funcType.getArgTypes().get(i);
            if (!isSubtype(callArgumentType, argumentType)) {
                throw new TypeException("Argument types do not match with function definition",
                        methodName.getRow(), methodName.getCol());
            }
        }

        if (funcType.getReturnTypeList().getVariableTypeList().size() > 0) {
            throw new TypeException("You cannot call a function with a return " +
                    "value as a procedure.", methodName.getRow(), methodName.getCol());
        } else {
            node.setType(new UnitType());
        }
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

        FunctionType funcType = (FunctionType) context.get(id);

        List<VariableType> argumentTypes = funcType.getArgTypes();
        List<Expression> passedArguments = node.getArguments();
        if (passedArguments.size() != argumentTypes.size()) {
            throw new TypeException("Too many arguments passed to " + id, id.getRow(), id.getCol());
        }

        for (int i = 0; i < passedArguments.size(); i++) {
            Expression argument = passedArguments.get(i);
            argument.accept(this);
            assert argument.getType() instanceof VariableType;
            VariableType argType = (VariableType) argument.getType();
            if (!isSubtype(argType, argumentTypes.get(i))) {
                throw new TypeException("Argument types do not match procedure definition", id.getRow(), id.getCol());
            }
        }

        if (funcType.getReturnTypeList().getVariableTypeList().size() > 0) {
            throw new TypeException("You cannot call a function with a return " +
                    "value as a procedure.", id.getRow(), id.getCol());
        }

        node.setType(new UnitType());
    }

    /**
     * Throws a TypeException if it encounters a cyclic inheritance
     */
    private void checkCyclicInheritance(ClassDeclaration cd) {
        Set<Identifier> seenClasses = new HashSet<>();
        ClassDeclaration currentClassDec = cd;
        while (currentClassDec.getParentIdentifier().isPresent()) {
            Identifier className = currentClassDec.getIdentifier();
            if (seenClasses.contains(className)) {
                throw new TypeException("Encountered cyclic inheritance in class " + className.getName(),
                        className.getRow(), className.getCol());
            }
            seenClasses.add(className);
            currentClassDec = classes.get(currentClassDec.getParentIdentifier().get());
        }
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
        // Populate the classes map first incase any functions use objects
        for (ClassDeclaration classDec : node.getClassDeclarations()) {
            Identifier className = classDec.getIdentifier();
            if (classes.containsKey(className)) {
                throw new TypeException("Class " + className.getName() + "declared multiple times");
            }
            classes.put(className, classDec);
        }

        // Check that all class parents are valid classes
        for (ClassDeclaration classDec : node.getClassDeclarations()) {
            if (classDec.getParentIdentifier().isPresent()) {
                Identifier parentIdentifier = classDec.getParentIdentifier().get();
                if (!classes.containsKey(parentIdentifier)) {
                    throw new TypeException("Class " + parentIdentifier.getName() + " does not exist",
                            parentIdentifier.getRow(), parentIdentifier.getCol());
                }
            }
        }

        // Make sure there's no cyclic inheritance
        for (ClassDeclaration classDec : node.getClassDeclarations()) {
            checkCyclicInheritance(classDec);
        }

        // Add all functions to the initial context
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


        // Look for an interface file for this file, doesn't need to exist
        Interface interface4120 = new Interface();
        String err = InterfaceParser.parseInterface(libPath, moduleName, interface4120);
        if (err == null) {
            // No error, so we'll check that the class declarations match the ones in this module
            for (ClassDeclaration cd : interface4120.getClassDeclarations()) {
                if (!classes.containsKey(cd.getIdentifier())) {
                    throw new TypeException("Interface does not match class declarations in this file"); // TODO
                }
                ClassDeclaration cd_ = classes.get(cd.getIdentifier());

                // Check Method Declarations match
                for (MethodDeclaration m : cd.getMethods()) {
                    if (!cd_.getMethods().contains(m)) {
                        throw new TypeException("Interface contains class methods that do not match"); // TODO loc
                    }
                }

                // Check inheritance matches
                if (!cd.getParentIdentifier().equals(cd_.getParentIdentifier())) {
                    throw new TypeException("Interface contains class whose parent does not match"); // TODO loc
                }
                // Ignore fields
            }

            // TODO: also check that the function declarations match?
        }
        if (!err.contains("not found")) {
            // Some error other than interface file not found
            throw new TypeException(err);
        }
        // Else interface file was not found, ignore and continue


        // Add function declarations from interface files
        for (UseStatement useStatement : node.getUseBlock()) {
            useStatement.accept(this);
        }

        // Add all global variables to context
        for (Assignment global : node.getGlobalVariables()) {
            assert global.getVariables().size() == 1;
            assert global.getVariables().get(0) instanceof TypedDeclaration;
            Expression expression = global.getExpression();
            TypedDeclaration td = (TypedDeclaration) global.getVariables().get(0);

            // Check to ensure a global has not been declared already
            if (contexts.peek().containsKey(td.getIdentifier())) {
                throw new TypeException("Global variable " + td.getIdentifier().getName() + " already declared",
                        td.getRow(), td.getCol());
            }
            // Check integer globals
            if (td.getDeclarationType().equals(new IntType())) {
                if (!(expression instanceof IntegerLiteral)) {
                    throw new TypeException("Global integer variables can only be initialized to literals",
                            expression.getRow(), expression.getCol());
                }
            }
            // Check boolean globals
            if (td.getDeclarationType().equals(new BoolType())) {
                if (!(expression instanceof BooleanLiteral)) {
                    throw new TypeException("Global boolean variables can only be initialized to literals",
                            expression.getRow(), expression.getCol());
                }
            }
            // Check object globals initialized to null
            if (td.getDeclarationType() instanceof ClassType) {
                if (!(expression instanceof Null)) {
                    throw new TypeException("Global object variables can only be initialized to null",
                            expression.getRow(), expression.getCol());
                }
            }
            // Check arraytype has valid init
            // Size is either a integer, or another global
            if (td.getDeclarationType() instanceof ArrayType) {
                if (!(expression instanceof Null)) {
                    throw new TypeException("Global array variables can only be initialized to null",
                            expression.getRow(), expression.getCol());
                }
                // Check each size init is an integer or another global that is an integer
                for (Expression size : td.getArraySizes()) {
                    if (size instanceof IntegerLiteral) {
                        continue;
                    }
                    if (size instanceof Identifier) {
                        Identifier sizeIdentifier = (Identifier) size;
                        for (Assignment otherGlobal : node.getGlobalVariables()) {
                            assert otherGlobal.getVariables().size() == 1;
                            assert otherGlobal.getVariables().get(0) instanceof TypedDeclaration;
                            TypedDeclaration otherTd = (TypedDeclaration) otherGlobal.getVariables().get(0);
                            if (otherTd.getIdentifier().equals(sizeIdentifier)) {
                                if (!otherTd.getDeclarationType().equals(new IntType())) {
                                    throw new TypeException("Array size must be an int",
                                            sizeIdentifier.getRow(), sizeIdentifier.getCol());
                                }
                                break;
                            }

                        }
                        continue;
                    }
                    throw new TypeException("Array sizes can only be initialized to integer literals or other global variables",
                            size.getRow(), size.getCol());
                }
            }

            contexts.peek().put(td.getIdentifier(), td.getDeclarationType());
        }

        // Second pass typechecks all the function bodies
        for (FunctionDeclaration functionDeclaration : node.getFunctionDeclarations()) {
            functionDeclaration.accept(this);
        }

        for (ClassDeclaration classDeclaration : node.getClassDeclarations()) {
            classDeclaration.accept(this);
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
            if (!isSubtype(type, functionReturnTypes.get(i))) {
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

    // TODO
    public void visit(This node) {
     // TODO
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
        VariableType type = node.getDeclarationType();
        if (!isValidVariableType(type)) {
            throw new TypeException("Invalid type", node.getRow(), node.getCol());
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
        // If we already parsed this interface, ignore
        if (parsedInterfaces.contains(interfaceName)) {
            return "";
        }
        parsedInterfaces.add(interfaceName);

        Interface interface69 = new Interface();
        String error = InterfaceParser.parseInterface(libPath,
                interfaceName, interface69);
        if (error != null) {
            return error;
        }

        // Read in each interface's use blocks recursively
        for(UseStatement use : interface69.getUseBlock()) {
            if (addInterface(libPath, use.getIdentifier().getName(), context) != null) {
                throw new TypeException(error); // TODO: minorly sketched out by these 3 lines
            }
        }

        // Read in each function declaration, add to context
        List<FunctionDeclaration> declarations = interface69.getFunctionDeclarations();
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

        // Save all classes
        for (ClassDeclaration cd : interface69.getClassDeclarations()) {
            classes.put(cd.getIdentifier(), cd);
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