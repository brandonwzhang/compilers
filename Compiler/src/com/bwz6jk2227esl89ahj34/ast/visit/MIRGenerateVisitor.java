package com.bwz6jk2227esl89ahj34.ast.visit;
import com.bwz6jk2227esl89ahj34.ast.*;
import com.bwz6jk2227esl89ahj34.ast.type.*;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.math.BigInteger;
import java.util.*;

public class MIRGenerateVisitor implements NodeVisitor {
    // The root of the MIR Tree
    private IRCompUnit root;
    // A stack of nodes, the top of which represents the current node.
    // Visit functions will modify the top of the current node
    private Stack<IRNode> generatedNodes;
    // The name of the program
    private String name;
    // Current class we're inside of
    private Optional<Identifier> currentClassID;
    // Counter to append to label strings.
    private long labelCounter = 0;
    // Map from class name to dispatch vector
    private Map<Identifier, List<MethodDeclaration>> dispatchVectors = new HashMap<>();
    // Map from class name to fields in a consistent order
    private Map<Identifier, List<Identifier>> classFields = new HashMap<>();
    // Map from class name to class
    private Map<Identifier, ClassDeclaration> classes = new HashMap<>();
    // Set of all global variables
    private Map<Identifier, VariableType> globalVariables = new HashMap<>();

    public MIRGenerateVisitor(String name) {
        this.name = name;
        generatedNodes = new Stack<>();
    }

    public IRCompUnit getRoot() {
        return root;
    }

    private String getFreshVariable() {
        return "temp" + (labelCounter++);
    }

  	/*
    	General paradigm:
      - We use a stack called generatedNodes that allows us to bypass our visit functions not returning anything.

      - First, declare the vars you'll need to generate the proper IRNode(s)
      - Call accept on this AST node's children (which pushes completed IRNodes into the stack)
      - Pop the needed values off the stack (assert), then create the IRNode(s) for this AST node and push onto the stack.
    */

    public void visit(ArrayIndex node) {
        // a[e]
        // Return IRMem that contains the value of the array a at index e
        // OR if out of bounds, a functioncall to out of bounds exception

        node.getArrayRef().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr array = (IRExpr)generatedNodes.pop();

        node.getIndex().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr index = (IRExpr)generatedNodes.pop();

        // Move array ref into a temp
        IRTemp arrTemp = new IRTemp(getFreshVariable());
        IRMove moveArrToTemp = new IRMove(arrTemp, array);

        // Move index into a temp
        IRTemp indexTemp = new IRTemp(getFreshVariable());
        IRMove moveIndexToTemp = new IRMove(indexTemp, index);


        IRMem length = new IRMem(new IRBinOp(OpType.SUB, arrTemp,
                new IRConst(Configuration.WORD_SIZE)), IRMem.MemType.IMMUTABLE);

        IRExpr result = new IRMem( new IRBinOp(
                OpType.ADD,
                new IRBinOp(OpType.MUL, indexTemp, new IRConst(Configuration.WORD_SIZE)),
                arrTemp
        ));

        // Check for out of bounds index
        IRLabel trueLabel = new IRLabel(getFreshVariable());
        IRLabel falseLabel = new IRLabel(getFreshVariable());
        IRLabel exitLabel = new IRLabel(getFreshVariable());

        IRCJump cjump = new IRCJump( // index < length && index >= 0
                new IRBinOp(OpType.AND,
                        new IRBinOp(OpType.LT, indexTemp, length),
                        new IRBinOp(OpType.GEQ, indexTemp, new IRConst(0))),
                trueLabel.name(),
                falseLabel.name()
        );

        IRSeq trueBody = new IRSeq(
                new IRJump(new IRName(exitLabel.name()))
        );

        IRExp outOfBoundsCall = new IRExp(new IRCall(new IRName("_I_outOfBounds_p")));

        IRSeq seq = new IRSeq(
                moveArrToTemp,
                moveIndexToTemp,
                cjump,
                trueLabel,
                trueBody,
                falseLabel,
                outOfBoundsCall,
                exitLabel
        );

        IRESeq eseq = new IRESeq(seq, result);

        generatedNodes.push(eseq);
    }

    public void visit(ArrayLiteral node) {
        // Overall return type is IRTemp t, of which IRMem(t) is index 0 of array

        // eseq
        // Call malloc for 8(n+1), the size of the arrayliteral, move result into a
        // Move n (length of arrayliteral) into a
        // Move a+8 into a
        // Return a

        List<IRExpr> arrayElements = new LinkedList<>();
        int length = node.getValues().size();
        for (Expression e : node.getValues()) {
            e.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            arrayElements.add((IRExpr)generatedNodes.pop());
        }

        String array = getFreshVariable(); // temp to store array

        List<IRStmt> stmts = new LinkedList<>();
        // Call to malloc
        IRCall malloc = new IRCall(new IRName("_I_alloc_i"), new IRConst(Configuration.WORD_SIZE * (length + 1)));
        IRMove storeArrayPtr = new IRMove(new IRTemp(array), malloc);

        // Save length in MEM(array)
        IRMove saveLength = new IRMove(new IRMem(new IRTemp(array)), new IRConst(length));
        // Shift array up to 0th index
        IRMove shift = new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD,
                new IRTemp(array), new IRConst(Configuration.WORD_SIZE)));

        stmts.add(storeArrayPtr);
        stmts.add(saveLength);
        stmts.add(shift);

        // Put elements into array spaces
        for (IRExpr e : arrayElements) {
            stmts.add(new IRMove(new IRMem(new IRTemp(array)), e)); // put array element in
            stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD,
                    new IRTemp(array), new IRConst(Configuration.WORD_SIZE)))); // add 8 to array pointer
        }

        // Move array pointer back to index 0
        stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.SUB,
                new IRTemp(array), new IRConst(Configuration.WORD_SIZE * length))));

        IRSeq seq = new IRSeq(stmts);
        IRESeq eseq = new IRESeq(seq, new IRTemp(array));

        generatedNodes.push(eseq);
    }

    private IRStmt singleAssignment(Assignable variable, IRExpr expr) {
        if (variable instanceof Underscore) {
            // Throw away if underscore
            return new IRExp(expr);
        } else if (variable instanceof TypedDeclaration) {
            // Since TypedDeclarations are treated as statements, we must
            // handle them separately
            TypedDeclaration typedDeclaration = (TypedDeclaration) variable;
            List<IRStmt> statements = new ArrayList<>();
            variable.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            statements.add((IRStmt) generatedNodes.pop());
            IRTemp temp = new IRTemp(typedDeclaration.getIdentifier().getName());
            IRMove move = new IRMove(temp, expr);
            statements.add(move);
            return new IRSeq(statements);
        }
        variable.accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRMove move = new IRMove((IRExpr) generatedNodes.pop(), expr);
        return move;
    }

    public void visit(Assignment node) {
        List<Assignable> variables = node.getVariables();
        Expression expression = node.getExpression();

        // If RHS is VariableType, we know there was no multiassign
        if (expression.getType() instanceof VariableType) {
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            generatedNodes.push(singleAssignment(variables.get(0), (IRExpr) generatedNodes.pop()));
            return;
        }
        // If RHS is VariableTypeList, we have to handle a multiassign
        assert expression instanceof FunctionCall;
        assert expression.getType() instanceof VariableTypeList;
        assert variables.size() == ((VariableTypeList) expression.getType()).getVariableTypeList().size();

        List<IRStmt> statements = new ArrayList<>();
        expression.accept(this);
        assert generatedNodes.peek() instanceof IRExpr;

        // First set the first variable to the value of the call (_RET0)
        statements.add(singleAssignment(variables.get(0), (IRExpr) generatedNodes.pop()));
        for (int i = 1; i < variables.size(); i++) {
            statements.add(singleAssignment(variables.get(i), new IRTemp(Configuration.ABSTRACT_RET_PREFIX + i)));
        }
        IRSeq seq = new IRSeq(statements);
        generatedNodes.push(seq);
    }

    public void visit(Binary node) {
        OpType optype;
        IRExpr left, right;
        VariableType nodeType = null;
        // get IR op type
        switch(node.getOp()){
            case PLUS:
                optype = OpType.ADD;
                break;
            case MINUS:
                optype = OpType.SUB;
                break;
            case TIMES:
                optype = OpType.MUL;
                break;
            case DIVIDE:
                optype = OpType.DIV;
                break;
            case MODULO:
                optype = OpType.MOD;
                break;
            case HIGH_MULT:
                optype = OpType.HMUL;
                break;
            case AND:
                optype = OpType.AND;
                break;
            case OR:
                optype = OpType.OR;
                break;
            case EQUAL:
                optype = OpType.EQ;
                break;
            case NOT_EQUAL:
                optype = OpType.NEQ;
                break;
            case LT:
                optype = OpType.LT;
                break;
            case LEQ:
                optype = OpType.LEQ;
                break;
            case GT:
                optype = OpType.GT;
                break;
            case GEQ:
                optype = OpType.GEQ;
                break;
            default:
                throw new RuntimeException("Unknown Binop Detected");
        }
        // gather translations of left and right expressions
        node.getLeft().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        left = (IRExpr)generatedNodes.pop();
        IRTemp leftTemp = new IRTemp(getFreshVariable());
        IRMove moveLeftToTemp = new IRMove(leftTemp, left);

        node.getRight().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        right = (IRExpr)generatedNodes.pop();
        IRTemp rightTemp = new IRTemp(getFreshVariable());
        IRMove moveRightToTemp = new IRMove(rightTemp, right);

        // array addition case
        if (optype == OpType.ADD && node.getLeft().getType() instanceof ArrayType) {
            assert node.getRight().getType() instanceof ArrayType;
            // get length of operands
            IRMem leftLength = new IRMem(new IRBinOp(OpType.SUB, leftTemp, new IRConst(Configuration.WORD_SIZE)));
            IRMem rightLength = new IRMem(new IRBinOp(OpType.SUB, rightTemp, new IRConst(Configuration.WORD_SIZE)));
            IRBinOp combinedLength = new IRBinOp(OpType.ADD, leftLength, rightLength);

            /* Allocate space for new array */
            String combinedArray = getFreshVariable();
            List<IRStmt> stmts = new LinkedList<>();
            stmts.add(moveLeftToTemp);
            stmts.add(moveRightToTemp);
            // call malloc
            IRCall malloc = new IRCall(new IRName("_I_alloc_i"),
                    new IRBinOp(OpType.MUL,
                            new IRBinOp(OpType.ADD, combinedLength, new IRConst(1)),
                            new IRConst(Configuration.WORD_SIZE)));
            IRMove storeArrayPtr = new IRMove(new IRTemp(combinedArray), malloc);
            // TODO make first index of malloc's return IMMUTABLE IRMem

            // save length in MEM(combinedArray)
            IRMove saveLength = new IRMove(new IRMem(new IRTemp(combinedArray)), combinedLength);
            // shift array up to 0th index
            IRMove shift = new IRMove(new IRTemp(combinedArray), new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(Configuration.WORD_SIZE)));
            // Store the start of the array to return
            IRTemp retArray = new IRTemp(getFreshVariable());

            stmts.add(storeArrayPtr);
            stmts.add(saveLength);
            stmts.add(shift);
            stmts.add(new IRMove(retArray, new IRTemp(combinedArray)));

            /* Insert elements into new array */
            // insert elements from left array
            String index = getFreshVariable();
            stmts.add(new IRMove(new IRTemp(index), new IRConst(0)));

            IRLabel headLabel = new IRLabel(getFreshVariable());
            IRLabel trueLabel = new IRLabel(getFreshVariable());
            IRLabel falseLabel = new IRLabel(getFreshVariable());

            IRBinOp guard = new IRBinOp(OpType.LT, new IRTemp(index), leftLength);
            IRCJump cjump = new IRCJump(guard, trueLabel.name(), falseLabel.name());

            IRBinOp leftElement = new IRBinOp(OpType.ADD,
                    new IRBinOp(OpType.MUL,
                            new IRTemp(index), new IRConst(Configuration.WORD_SIZE)),
                    leftTemp);
            IRMove trueMove1 = new IRMove(new IRMem(new IRTemp(combinedArray)), new IRMem(leftElement));
            IRMove trueMove2 = new IRMove(new IRTemp(combinedArray),
                    new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(Configuration.WORD_SIZE)));
            IRMove trueMove3 = new IRMove(new IRTemp(index),
                    new IRBinOp(OpType.ADD, new IRTemp(index), new IRConst(1)));
            IRJump headJump = new IRJump(new IRName(headLabel.name()));

            stmts.add(headLabel);
            stmts.add(cjump);
            stmts.add(trueLabel);
            stmts.add(trueMove1);
            stmts.add(trueMove2);
            stmts.add(trueMove3);
            stmts.add(headJump);
            stmts.add(falseLabel);

            // insert elements from right array
            stmts.add(new IRMove(new IRTemp(index), new IRConst(0))); // reset index to 0

            IRLabel headLabel2 = new IRLabel(getFreshVariable());
            IRLabel trueLabel2 = new IRLabel(getFreshVariable());
            IRLabel falseLabel2 = new IRLabel(getFreshVariable());

            IRBinOp guard2 = new IRBinOp(OpType.LT, new IRTemp(index), rightLength);
            IRCJump cjump2 = new IRCJump(guard2, trueLabel2.name(), falseLabel2.name());

            IRBinOp rightElement = new IRBinOp(OpType.ADD,
                    new IRBinOp(OpType.MUL,
                            new IRTemp(index), new IRConst(Configuration.WORD_SIZE)),
                    rightTemp);
            IRMove trueMove1_ = new IRMove(new IRMem(new IRTemp(combinedArray)), new IRMem(rightElement));
            IRMove trueMove2_ = new IRMove(new IRTemp(combinedArray),
                    new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(Configuration.WORD_SIZE)));
            IRMove trueMove3_ = new IRMove(new IRTemp(index),
                    new IRBinOp(OpType.ADD, new IRTemp(index), new IRConst(1)));
            IRJump headJump2 = new IRJump(new IRName(headLabel2.name()));

            stmts.add(headLabel2);
            stmts.add(cjump2);
            stmts.add(trueLabel2);
            stmts.add(trueMove1_);
            stmts.add(trueMove2_);
            stmts.add(trueMove3_);
            stmts.add(headJump2);
            stmts.add(falseLabel2);

            IRSeq seq = new IRSeq(stmts);
            IRESeq eseq = new IRESeq(seq, retArray);

            generatedNodes.push(eseq);
            return;
        }

        // short circuit cases
        if (optype == OpType.AND) {
            IRTemp result = new IRTemp(getFreshVariable());
            IRLabel continuelabel = new IRLabel(getFreshVariable());
            IRLabel skiplabel = new IRLabel(getFreshVariable());
            IRCJump cjump = new IRCJump(left, continuelabel.name(), skiplabel.name());
            IRMove initmove = new IRMove(result, new IRConst(0));
            IRMove truemove = new IRMove(result, right);

            IRLabel endlabel = new IRLabel(getFreshVariable());
            IRJump jumpToEndFromTrue = new IRJump(new IRName(endlabel.name()));

            IRSeq seq = new IRSeq(Arrays.asList(
                    initmove,
                    cjump,
                    continuelabel,
                    truemove,
                    jumpToEndFromTrue,
                    skiplabel,
                    endlabel));
            IRESeq eseq = new IRESeq(seq, result);
            generatedNodes.push(eseq);
            return;
        }
        if (optype == OpType.OR) {
            // same as above, but with left evaluating to true (and a default result of 1 (true)
            IRTemp result = new IRTemp(getFreshVariable());
            IRMove initmove = new IRMove(result, new IRConst(1));
            IRLabel label1 = new IRLabel(getFreshVariable());
            IRLabel label2 = new IRLabel(getFreshVariable());
            IRLabel endLabel = new IRLabel(getFreshVariable());
            IRCJump cjump = new IRCJump(left, label1.name(), label2.name());
            IRJump jumpToEnd = new IRJump(new IRName(endLabel.name()));
            IRMove move = new IRMove(result, right);
            IRSeq seq = new IRSeq(Arrays.asList(
                    initmove,
                    cjump,
                    label1,
                    jumpToEnd,
                    label2,
                    move,
                    endLabel
            ));
            IRESeq eseq = new IRESeq(seq, result);
            generatedNodes.push(eseq);
            return;
        }
        // non short-circuit case
        generatedNodes.push(new IRBinOp(optype, left, right));

    }

    // not sure about this
    public void visit(BlockList node) {
        List<Block> blockList = node.getBlocks();
        List<IRStmt> stmtList = new ArrayList<>();

        for (Block block : blockList) {
            block.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            stmtList.add((IRStmt) generatedNodes.pop());
        }

        IRStmt body = new IRSeq(stmtList);
        generatedNodes.push(body);
    }

    public void visit(BooleanLiteral node) {
        generatedNodes.push(new IRConst(node.getValue() ? 1l : 0l));
    }

    public void visit(Break node) {
        // TODO:
    }

    public void visit(CharacterLiteral node) {
        generatedNodes.push(new IRConst((long) node.getValue()));
    }

    public void visit(ClassDeclaration node) {
        // Handled in visit(Program node)
    }

    public void visit(FunctionCall node) {
        // If we're inside a class, check if the function called is a method
        if (currentClassID.isPresent()) {
            Identifier className = currentClassID.get();
            int methodIndex = getMethodIndex(className, node.getIdentifier());
            if (methodIndex >= 0) {
                // Generate a ObjectFunctionCall if we're calling a method
                ObjectFunctionCall objectFunctionCall =
                        new ObjectFunctionCall(node.getIdentifier(), new Identifier("this"), node.getArguments());
                objectFunctionCall.accept(this);
                return;
            }
        }
        List<IRExpr> arguments = new ArrayList<>();
        List<VariableType> argTypeList = new ArrayList<>();
        // Store all arguments in a list
        for (Expression expression : node.getArguments()) {
            assert expression.getType() instanceof VariableType;
            argTypeList.add((VariableType) expression.getType());
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr argument = (IRExpr) generatedNodes.pop();
            arguments.add(argument);
        }

        /* Special case for length(arr) */
        if (node.getIdentifier().getName().equals("length")) {
            assert arguments.size() == 1;
            IRExpr arrayArg = arguments.get(0);

            IRMem length = new IRMem(new IRBinOp(OpType.SUB, arrayArg, new IRConst(Configuration.WORD_SIZE)));
            generatedNodes.push(length);

            return;
        }

        String irFunctionName = "";
        if (node.getType() instanceof VariableTypeList) {
            FunctionType funcType = new FunctionType(argTypeList, (VariableTypeList) node.getType());
            FunctionDeclaration tempFuncDec = new FunctionDeclaration(node.getIdentifier(), funcType, null, null);
            irFunctionName = Util.getIRFunctionName(tempFuncDec);
        } else {
            assert node.getType() instanceof VariableType;
            List<VariableType> retTypes = new ArrayList<>(Arrays.asList((VariableType) node.getType()));
            FunctionType funcType = new FunctionType(argTypeList, new VariableTypeList(retTypes));
            FunctionDeclaration tempFuncDec = new FunctionDeclaration(node.getIdentifier(), funcType, null, null);
            irFunctionName = Util.getIRFunctionName(tempFuncDec);
        }


        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(new IRName(irFunctionName), arguments);
        generatedNodes.push(call);
    }

    public void visit(FunctionDeclaration node) {
        // If we have a procedure, we need to make sure the last statement is return
        if (node.getFunctionType().getReturnTypeList().getVariableTypeList().isEmpty()) {
            List<Block> blocks = node.getBlockList().getBlocks();
            if (blocks.isEmpty() || !(blocks.get(blocks.size() - 1) instanceof ReturnStatement)) {
                blocks.add(new ReturnStatement(new LinkedList<>()));
            }
        }
        node.getBlockList().accept(this);

        assert generatedNodes.peek() instanceof IRSeq;
        IRSeq body = (IRSeq) generatedNodes.pop();
        List<IRStmt> fullBody = new ArrayList<>();
        for (int i = 0; i < node.getArgumentIdentifiers().size(); i++) {
            IRTemp varTemp = new IRTemp(node.getArgumentIdentifiers().get(i).getName());
            fullBody.add(new IRMove(varTemp, new IRTemp(Configuration.ABSTRACT_ARG_PREFIX + i)));
        }
        fullBody.addAll(body.stmts());

        IRFuncDecl irfd = new IRFuncDecl(Util.getIRFunctionName(node), new IRSeq(fullBody));
        generatedNodes.push(irfd);
    }

    public void visit(Identifier node) {
        // if we're inside a class, check if there's a local variable
        if (currentClassID.isPresent()) {
            // if classFields contains the field, simplify to "this.field", and visit
            Identifier className = currentClassID.get();
            List<Identifier> fields = classFields.get(className);
            int index = fields.indexOf(node);
            if (index != -1) {
                ObjectField thisdot = new ObjectField(new Identifier("this"), node);
                thisdot.accept(this);
                return;
            }
        }
        // else we could be 1) not inside a class 2) using a variable that's not an instance var
        // use the identifier as the name of the temp
        // If the identifier is a global variable, we have to return its memory location
        if (globalVariables.containsKey(node)) {
            generatedNodes.push(new IRMem(
                    new IRName(Util.getIRGlobalVariableName(node, globalVariables.get(node)))));
            return;
        }
        generatedNodes.push(new IRTemp(node.getName()));
    }
    public void visit(IfStatement node) {
        node.getGuard().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr guard = (IRExpr) generatedNodes.pop();

        String trueLabelName = getFreshVariable();
        IRLabel trueLabel = new IRLabel(trueLabelName);
        node.getTrueBlock().accept(this);
        assert generatedNodes.peek() instanceof IRStmt;
        IRStmt trueBlock = (IRStmt) generatedNodes.pop();

        // We need a label for the end of the if statement to ensure
        // that both blocks don't execute
        String endLabelName = getFreshVariable();
        IRLabel endLabel = new IRLabel(endLabelName);

        List<IRStmt> statements = new ArrayList<>();

        if (node.getFalseBlock().isPresent()) {
            String falseLabelName = getFreshVariable();
            IRLabel falseLabel = new IRLabel(falseLabelName);
            IRCJump cjump = new IRCJump(guard, trueLabelName, falseLabelName);
            statements.add(cjump);
            statements.add(trueLabel);
            statements.add(trueBlock);
            statements.add(new IRJump(new IRName(endLabelName))); //added by jihun
                                                            // to fix reordering
            statements.add(falseLabel);
            node.getFalseBlock().get().accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            IRStmt falseBlock = (IRStmt) generatedNodes.pop();
            statements.add(falseBlock);
            statements.add(new IRJump(new IRName(endLabelName)));
        } else { // the falselabel is to make cjumps consistent
            String falseLabelName = getFreshVariable();
            IRLabel falseLabel = new IRLabel(falseLabelName);
            IRCJump cjump = new IRCJump(guard, trueLabelName, falseLabelName);
            statements.add(cjump);
            statements.add(trueLabel);
            statements.add(trueBlock);
            statements.add(new IRJump(new IRName(endLabelName)));
            statements.add(falseLabel);
            statements.add(new IRJump(new IRName(endLabelName)));
        }
        statements.add(endLabel);
        generatedNodes.push(new IRSeq(statements));
    }

    public void visit(IntegerLiteral node) {
        long value = Long.parseLong(node.getValue());
        generatedNodes.push(new IRConst(value));
    }

    public void visit(MethodDeclaration node) {
        FunctionDeclaration fd = node.getFunctionDeclaration();
        // Add the "this" argument to the list of arguments
        fd.getArgumentIdentifiers().add(0, new Identifier("this"));

        fd.accept(this);
        assert generatedNodes.peek() instanceof IRFuncDecl;
        IRFuncDecl funcDecl = (IRFuncDecl) generatedNodes.pop();
        // Replace the function name with the method name
        IRFuncDecl methodDecl = new IRFuncDecl(Util.getIRMethodName(node), funcDecl.body());
        generatedNodes.push(methodDecl);
    }

    public void visit(Null node) {
        // Null is just the pointer to memory location 0
        generatedNodes.push(new IRMem(new IRConst(0)));
    }

    public void visit(ObjectField node) {
        // Find the index of this field in the object
        // from typechecking, we are guaranteed that the object class is correct, and
        // that the field is a valid field in the object
        Identifier objectClass = ((ClassType) node.getObject().getType()).getIdentifier();
        List<Identifier> fields = classFields.get(objectClass);
        int fieldIndex = fields.indexOf(node.getField());
        assert fieldIndex >= 0;

        // Calculate the memory address of this field
        node.getObject().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr object = (IRExpr) generatedNodes.pop();
        IRBinOp fieldAddress = new IRBinOp(OpType.ADD, object,
                new IRConst(Configuration.WORD_SIZE * fieldIndex));

        generatedNodes.push(new IRMem(fieldAddress));
    }

    /**
     * Returns the index of the method in the dispatch vector. Returns -1 if not found.
     */
    private int getMethodIndex(Identifier objectClass, Identifier methodIdentifier) {
        int methodIndex = -1;
        List<MethodDeclaration> dispatchVector = dispatchVectors.get(objectClass);
        assert dispatchVector != null;
        for (int i = 0; i < dispatchVector.size(); i++) {
            MethodDeclaration md = dispatchVector.get(i);
            if (md.getFunctionDeclaration().getIdentifier().equals(methodIdentifier)) {
                methodIndex = i;
                break;
            }
        }
        return methodIndex;
    }

    public void visit(ObjectFunctionCall node) {
        // First, move the object into a temp
        node.getObject().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr object = (IRExpr) generatedNodes.pop();

        IRTemp objectTemp = new IRTemp(getFreshVariable());
        IRMove move = new IRMove(objectTemp, object);

        // Store all arguments in a list
        List<IRExpr> arguments = new ArrayList<>();
        // Add "this" to the argument list
        arguments.add(objectTemp);
        List<VariableType> argTypeList = new ArrayList<>();
        for (Expression expression : node.getArguments()) {
            assert expression.getType() instanceof VariableType;
            argTypeList.add((VariableType) expression.getType());
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr argument = (IRExpr) generatedNodes.pop();
            arguments.add(argument);
        }

        IRMem dispatchVectorLocation = new IRMem(objectTemp);
        assert node.getObject().getType() instanceof ClassType;
        Identifier objectClass = ((ClassType) node.getObject().getType()).getIdentifier();
        int methodIndex = getMethodIndex(objectClass, node.getIdentifier());
        assert methodIndex >= 0;
        IRExpr method = new IRMem(new IRBinOp(OpType.ADD, dispatchVectorLocation,
                new IRConst(Configuration.WORD_SIZE * methodIndex)));


        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(method, arguments);
        IRESeq eseq = new IRESeq(move, call);
        generatedNodes.push(eseq);
    }

    public void visit(ObjectInstantiation node) {
        Identifier classIdentifier = node.getClassIdentifier();
        IRCall malloc = new IRCall(new IRName("_I_alloc_i"), new IRName("_I_size_" + classIdentifier.getName()));
        List<IRStmt> stmts = new LinkedList<>();
        // Move the result of the call into a temp
        IRTemp objectTemp = new IRTemp(getFreshVariable());
        stmts.add(new IRMove(objectTemp, malloc));
        // Add dispatch vector to first slot of object
        stmts.add(new IRMove(new IRMem(objectTemp), new IRName("_I_vt_" + classIdentifier.getName())));
        generatedNodes.push(new IRESeq(new IRSeq(stmts), objectTemp));
    }

    public void visit(ObjectProcedureCall node) {
        // First, move the object into a temp
        node.getObject().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr object = (IRExpr) generatedNodes.pop();

        IRTemp objectTemp = new IRTemp(getFreshVariable());
        IRMove move = new IRMove(objectTemp, object);

        // Store all arguments in a list
        List<IRExpr> arguments = new ArrayList<>();
        // Add "this" to the argument list
        arguments.add(objectTemp);
        List<VariableType> argTypeList = new ArrayList<>();
        for (Expression expression : node.getArguments()) {
            assert expression.getType() instanceof VariableType;
            argTypeList.add((VariableType) expression.getType());
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr argument = (IRExpr) generatedNodes.pop();
            arguments.add(argument);
        }

        IRMem dispatchVectorLocation = new IRMem(objectTemp);
        Identifier objectClass = ((ClassType) node.getObject().getType()).getIdentifier();
        int methodIndex = getMethodIndex(objectClass, node.getIdentifier());
        IRExpr method = new IRMem(new IRBinOp(OpType.ADD, dispatchVectorLocation,
                new IRConst(Configuration.WORD_SIZE * methodIndex)));


        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(method, arguments);
        IRSeq seq = new IRSeq(move, new IRExp(call));
        generatedNodes.push(seq);
    }

    public void visit(ProcedureCall node) {
        // If we're inside a class, check if the procedure called is a method
        if (currentClassID.isPresent()) {
            Identifier className = currentClassID.get();
            int methodIndex = getMethodIndex(className, node.getIdentifier());
            if (methodIndex >= 0) {
                // Generate a ObjectProcedureCall if we're calling a method
                ObjectProcedureCall objectProcedureCall =
                        new ObjectProcedureCall(node.getIdentifier(), new Identifier("this"), node.getArguments());
                objectProcedureCall.accept(this);
                return;
            }
        }
        List<IRExpr> arguments = new ArrayList<>();
        List<VariableType> argTypeList = new ArrayList<>();
        // Store all arguments in a list
        for (Expression expression : node.getArguments()) {
            assert expression.getType() instanceof VariableType;
            argTypeList.add((VariableType) expression.getType());
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr argument = (IRExpr) generatedNodes.pop();
            arguments.add(argument);
        }

        FunctionType funcType = new FunctionType(argTypeList, new VariableTypeList(new ArrayList<>()));
        FunctionDeclaration tempFuncDec = new FunctionDeclaration(node.getIdentifier(), funcType, null, null);

        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(new IRName(Util.getIRFunctionName(tempFuncDec)), arguments);
        // We need to throw out the return value;
        generatedNodes.push(new IRExp(call));
    }

    private void addFields(ClassDeclaration cd) {
        List<Identifier> fields = new LinkedList<>();

        for (TypedDeclaration td : cd.getFields()) {
            Identifier field = td.getIdentifier();
            fields.add(field);
        }

        // Add this class's fields to the map
        classFields.put(cd.getIdentifier(), fields);
    }

    /**
     * Creates the dispatch vector for a class and adds it to dispatchVectors
     */
    private void addDispatchVector(ClassDeclaration cd) {
        if (dispatchVectors.containsKey(cd.getIdentifier())) {
            // The dispatch vector for this class has already been computed
            return;
        }
        List<MethodDeclaration> dispatchVector = new LinkedList<>();

        if (cd.getParentIdentifier().isPresent()) {
            Identifier parentIdentifier = cd.getParentIdentifier().get();
            addDispatchVector(classes.get(parentIdentifier));
            // Add all the parent class's methods to the dispatch vector
            dispatchVector.addAll(dispatchVectors.get(parentIdentifier));
        }

        for (MethodDeclaration md : cd.getMethods()) {
            boolean methodReplaced = false;
            String methodName = md.getFunctionDeclaration().getIdentifier().getName();
            for (int i = 0; i < dispatchVector.size(); i++) {
                String existingMethodName =
                        dispatchVector.get(i).getFunctionDeclaration().getIdentifier().getName();
                if (methodName.equals(existingMethodName)) {
                    dispatchVector.set(i, md);
                    methodReplaced = true;
                }
            }
            if (!methodReplaced) {
                dispatchVector.add(md);
            }
        }

        // Add this class's dispatch vector to the map
        dispatchVectors.put(cd.getIdentifier(), dispatchVector);
    }

    /**
     * Constructs an adjacency list representing the class hierarchy. Parent
     * classes point to their subclasses.
     */
    private Map<Identifier, List<Identifier>> constructClassHierarchyGraph(List<ClassDeclaration> cds) {
        Map<Identifier, List<Identifier>> graph = new HashMap<>();
        // First, populate the map with empty lists for each class
        for (ClassDeclaration cd : cds) {
            graph.put(cd.getIdentifier(), new LinkedList<>());
        }
        // Then, determine the direct subclasses for each class by adding each
        // class to its parent's adjacency list
        for (ClassDeclaration cd : cds) {
            if (cd.getParentIdentifier().isPresent()) {
                graph.get(cd.getParentIdentifier().get()).add(cd.getIdentifier());
            }
        }
        return graph;
    }

    /**
     * Construct the functions to initialize the size of each class.
     * Each class calls the initialization for its subclasses after it has been
     * initialized.
     */
    private List<IRFuncDecl> constructClassInitializationFunctions(List<ClassDeclaration> cds) {
        // We need the class hierarchy to determine the order in which we call functions
        Map<Identifier, List<Identifier>> hierarchyGraph = constructClassHierarchyGraph(cds);

        List<IRFuncDecl> functions = new LinkedList<>();
        for (ClassDeclaration cd : cds) {
            List<IRStmt> stmts = new LinkedList<>();
            // If the class has a parent class, we add the number of fields * WORD_SIZE to
            // the size of the parent class.
            // Otherwise, we add it to 1.
            Identifier identifier = cd.getIdentifier();
            IRExpr fieldSize = new IRConst(classFields.get(identifier).size() * Configuration.WORD_SIZE);
            // Either the size of the parent or 1 for the base size of the object
            IRExpr baseSize = cd.getParentIdentifier().isPresent() ?
                    new IRMem(new IRName("_I_size_" + cd.getParentIdentifier().get().getName())) : new IRConst(1);
            IRExpr size = new IRBinOp(OpType.ADD, fieldSize, baseSize);
            stmts.add(new IRMove(new IRMem(new IRName("_I_size_" + identifier.getName())), size));

            // We need to call the initialization functions for subclasses
            List<Identifier> subclasses = hierarchyGraph.get(identifier);
            for (Identifier subclass : subclasses) {
                stmts.add(new IRExp(new IRCall(new IRName("_I_init_" + subclass.getName()))));
            }
            functions.add(new IRFuncDecl("_I_init_" + identifier.getName(), new IRSeq(stmts)));
        }

        return functions;
    }

    /**
     * Construct the function to initialize all global variables
     */
    private IRFuncDecl constructGlobalInitializationFunction(List<Assignment> assignments) {
        List<IRStmt> stmts = new LinkedList<>();
        // First initialize all ints, bools, and classes
        for (Assignment assignment : assignments) {
            assert assignment.getVariables().size() == 1;
            assert assignment.getVariables().get(0) instanceof TypedDeclaration;
            TypedDeclaration td = (TypedDeclaration) assignment.getVariables().get(0);
            if (td.getDeclarationType() instanceof ArrayType) {
                // Ignore arrays for now
                continue;
            }
            assignment.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            stmts.add((IRStmt) generatedNodes.pop());
        }
        // Initialize all arrays after in case they rely on values of ints
        for (Assignment assignment : assignments) {
            assert assignment.getVariables().size() == 1;
            assert assignment.getVariables().get(0) instanceof TypedDeclaration;
            TypedDeclaration td = (TypedDeclaration) assignment.getVariables().get(0);
            if (!(td.getDeclarationType() instanceof ArrayType)) {
                // All other types have been initialized already
                continue;
            }
            assignment.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            stmts.add((IRStmt) generatedNodes.pop());
        }
        return new IRFuncDecl("_I_init_" + name, new IRSeq(stmts));
    }


    public void visit(Program node) {
        Map<String, IRFuncDecl> functions = new LinkedHashMap<>();

        // Populate classes
        for (ClassDeclaration cd : node.getClassDeclarations()) {
            classes.put(cd.getIdentifier(), cd);
        }

        // Populate dispatchVectors and classFields
        for (ClassDeclaration cd : node.getClassDeclarations()) {
            addDispatchVector(cd);
            addFields(cd);
        }

        // Add all global variables to the globalVariables set
        for (Assignment global : node.getGlobalVariables()) {
            assert global.getVariables().size() == 1;
            assert global.getVariables().get(0) instanceof TypedDeclaration;
            TypedDeclaration td = (TypedDeclaration) global.getVariables().get(0);
            globalVariables.put(td.getIdentifier(), td.getDeclarationType());
        }

        // Add methods to functions map
        for (ClassDeclaration cd : node.getClassDeclarations()) {
            // set the currentClassID in case we use instance variables later
            currentClassID = Optional.of(cd.getIdentifier());
            for (MethodDeclaration md : cd.getMethods()) {
                md.accept(this);
                assert generatedNodes.peek() instanceof IRFuncDecl;
                functions.put(Util.getIRMethodName(md), (IRFuncDecl) generatedNodes.pop());
            }
            currentClassID = Optional.empty();
        }

        // Add functions to functions map
        for (FunctionDeclaration fd : node.getFunctionDeclarations()) {
            fd.accept(this);
            assert generatedNodes.peek() instanceof IRFuncDecl;
            functions.put(Util.getIRFunctionName(fd), (IRFuncDecl) generatedNodes.pop());
        }

        // Construct all of the initialization functions
        List<IRFuncDecl> classInitializationFunctions = constructClassInitializationFunctions(node.getClassDeclarations());
        for (IRFuncDecl funcDecl : classInitializationFunctions) {
            functions.put(funcDecl.name(), funcDecl);
        }
        IRFuncDecl globalInitializationFunction = constructGlobalInitializationFunction(node.getGlobalVariables());
        functions.put(globalInitializationFunction.name(), globalInitializationFunction);

        // Add initializtion functions to the ctors list
        List<String> ctors = new LinkedList<>();
        // We always need to call the global initialization function
        ctors.add(globalInitializationFunction.name());
        // We only need to call the class initialization functions that won't be
        // called by other initialization functions (the roots of the class hierarchy).
        Map<Identifier, List<Identifier>> classHierarchy = constructClassHierarchyGraph(node.getClassDeclarations());
        for (ClassDeclaration cd : node.getClassDeclarations()) {
            Identifier identifier = cd.getIdentifier();
            boolean isRoot = true;
            for (List<Identifier> subclasses : classHierarchy.values()) {
                if (subclasses.contains(identifier)) {
                    isRoot = false;
                }
            }
            if (isRoot) {
                ctors.add("_I_init_" + identifier.getName());
            }
        }

        root = new IRCompUnit(name, functions, ctors);
        assert generatedNodes.empty();
    }

    public void visit(ReturnStatement node) {
        List<Expression> returnValues = node.getValues();
        List<IRStmt> statements = new LinkedList<>();
        List<IRTemp> temps = new LinkedList<>();
        for (int i = 0; i < returnValues.size(); i++) {
            Expression returnValue = returnValues.get(i);
            returnValue.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr irReturnValue = (IRExpr) generatedNodes.pop();
            IRTemp temp = new IRTemp(getFreshVariable());
            statements.add(new IRMove(temp, irReturnValue));
            temps.add(temp);
        }
        for (int i = 0; i < temps.size(); i++) {
            statements.add(new IRMove(new IRTemp(Configuration.ABSTRACT_RET_PREFIX + i),
                    temps.get(i)));
        }
        statements.add(new IRReturn());

        generatedNodes.push(new IRSeq(statements));
    }
    public void visit(StringLiteral node) {
        // StringLiteral = int ArrayLiteral
        List<Character> charList = Util.backslashMergedCharList(node.getValue());
        List<Expression> chars = new LinkedList<>();
        for (char c : charList) {
            chars.add(new CharacterLiteral(c));
        }

        ArrayLiteral arrayliteral = new ArrayLiteral(chars);
        arrayliteral.accept(this);
    }

    public IRBinOp scaleByWordSize(IRExpr expr) {
        return new IRBinOp(OpType.MUL, expr, new IRConst(Configuration.WORD_SIZE));
    }

    public List<IRStmt> initializeArray(IRTemp parentArrayPointer, IRExpr parentArrayIndex, List<IRExpr> lengths, int index) {
        List<IRStmt> statements = new ArrayList<>();
        IRTemp length = new IRTemp(getFreshVariable());
        statements.add(new IRMove(length, lengths.get(index)));
        // Allocate memory for this array
        IRCall malloc = new IRCall(new IRName("_I_alloc_i"), scaleByWordSize(new IRBinOp(OpType.ADD, length, new IRConst(1))));
        // Pointer for this array
        IRTemp arrayTemp = new IRTemp(getFreshVariable());
        statements.add(new IRMove(arrayTemp, malloc));
        // Add the length to before the first element of the array
        statements.add(new IRMove(new IRMem(arrayTemp), length));
        // Move pointer to the first element of the array
        statements.add(new IRMove(arrayTemp, new IRBinOp(OpType.ADD, arrayTemp, new IRConst(Configuration.WORD_SIZE))));
        if (index == 0) {
            // If we're at the first index, we just need to put the pointer to the array in the given temp
            statements.add(new IRMove(parentArrayPointer, arrayTemp));
        } else {
            // Store the pointer to this array in the parent array
            IRMem parentMem = new IRMem(new IRBinOp(OpType.ADD, parentArrayPointer, scaleByWordSize(parentArrayIndex)));
            statements.add(new IRMove(parentMem, arrayTemp));
        }
        // If we're at the last element, we don't need to add more subarrays
        if (index == lengths.size() - 1) {
            return statements;
        }
        // Iterate through the array and recursively allocate memory for subarrays
        IRLabel headLabel = new IRLabel(getFreshVariable());
        IRLabel trueLabel = new IRLabel(getFreshVariable());
        IRLabel exitLabel = new IRLabel(getFreshVariable());
        IRTemp counter = new IRTemp(getFreshVariable());
        statements.add(new IRMove(counter, new IRConst(0)));
        IRExpr guard = new IRBinOp(OpType.LT, counter, length);
        List<IRStmt> allocateSubarrays = initializeArray(arrayTemp, counter, lengths, index + 1);
        List<IRStmt> loopStatements = new ArrayList<>();
        loopStatements.add(headLabel);
        loopStatements.add(new IRCJump(guard, trueLabel.name(), exitLabel.name()));
        loopStatements.add(trueLabel);
        loopStatements.add(new IRSeq(allocateSubarrays));
        loopStatements.add(new IRMove(counter, new IRBinOp(OpType.ADD, counter, new IRConst(1))));
        loopStatements.add(new IRJump(new IRName(headLabel.name())));
        loopStatements.add(exitLabel);
        statements.add(new IRSeq(loopStatements));
        return statements;
    }

    public void visit(This node) {
        // TODO
    }

    public void visit(TypedDeclaration node) {
        if (node.getArraySizes().size() == 0) {
            // Initialize the variable to 0
            generatedNodes.push(new IRMove(new IRTemp(node.getIdentifier().getName()), new IRConst(0)));
            return;
        }
        // In the case where TypedDeclaration is part of an assignment, it will be handled separately
        // We only concern with a standalone declaration here (x:int[], y:bool[4], z:int, etc)
        // We'll store the pointer to the initialized array in the temp of the variable name
        // else do nothing
        List<Expression> arraySizeList = node.getArraySizes();
        List<IRExpr> lengths = new ArrayList<>();
        List<IRStmt> statements = new ArrayList<>();
        for (Expression arraySize : arraySizeList) {
            arraySize.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr lengthExpr = (IRExpr) generatedNodes.pop();
            IRTemp lengthTemp = new IRTemp(getFreshVariable());
            statements.add(new IRMove(lengthTemp, lengthExpr));
            lengths.add(lengthTemp);
        }
        String variableName = node.getIdentifier().getName();
        IRTemp array = new IRTemp(variableName);
        statements.addAll(initializeArray(array, null, lengths, 0));
        generatedNodes.push(new IRSeq(statements));
    }
    public void visit(Unary node) {
        // not --> XOR with 1
        // negate --> 0 minus that

        // Construct an IRBinary with a constant value as the other operand
        IRBinOp.OpType optype;
        IRExpr left;
        IRExpr right;

        switch (node.getOp()) {
            case MINUS:
                optype = IRBinOp.OpType.SUB;
                break;
            case NOT:
                optype = IRBinOp.OpType.XOR;
                break;
            default:
                throw new RuntimeException("Unknown UnaryOperator");
        }
        // As a convention, we will make the left the constant

        // however, we will check for a hard cose case in which
        // the smallest value possible is used; we need this because
        // the actual value exceeds the max value for a long, and if
        // we unwrap it from the negative sign then it will get exposed
        if (optype == IRBinOp.OpType.SUB &&
                node.getExpression().equals(new IntegerLiteral("9223372036854775808"))) {
            generatedNodes.push(new IRConst(new BigInteger("-9223372036854775808").longValue()));
        } else {
            node.getExpression().accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            right = (IRExpr) generatedNodes.pop();

            if (optype == IRBinOp.OpType.SUB) {
                left = new IRConst(0);
            } else if (optype == IRBinOp.OpType.XOR) {
                left = new IRConst(1);
            } else {
                throw new RuntimeException("Invalid BinOp");
            }

            assert left != null;
            generatedNodes.push(new IRBinOp(optype, left, right));
        }
    }
    public void visit(Underscore node) {
        // Handled in Assignment
        return;
    }
    public void visit(UseStatement node) {
        return;
    }
    public void visit(WhileStatement node) {
        // Create labels
        IRLabel headLabel = new IRLabel(getFreshVariable());
        IRLabel trueLabel = new IRLabel(getFreshVariable());
        IRLabel exitLabel = new IRLabel(getFreshVariable());

        // Perform translations
        node.getGuard().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr guard = (IRExpr)generatedNodes.pop();

        node.getBlock().accept(this);
        assert generatedNodes.peek() instanceof IRStmt;
        IRStmt body = (IRStmt)generatedNodes.pop();

        IRSeq seq = new IRSeq(
                headLabel,
                new IRCJump(guard, trueLabel.name(), exitLabel.name()),
                trueLabel,
                body,
                new IRJump(new IRName(headLabel.name())),
                exitLabel);

        generatedNodes.push(seq);
    }
}
