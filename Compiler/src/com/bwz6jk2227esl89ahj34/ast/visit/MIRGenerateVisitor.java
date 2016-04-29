package com.bwz6jk2227esl89ahj34.ast.visit;
import com.bwz6jk2227esl89ahj34.ast.*;
import com.bwz6jk2227esl89ahj34.ast.type.VariableType;
import com.bwz6jk2227esl89ahj34.ast.type.VariableTypeList;
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
    // Counter to append to label strings.
    private long labelCounter = 0;

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

        // Check for out of bounds index
        IRTemp result = new IRTemp(getFreshVariable());
        IRLabel trueLabel = new IRLabel(getFreshVariable());
        IRLabel falseLabel = new IRLabel(getFreshVariable());
        IRLabel exitLabel = new IRLabel(getFreshVariable());

        // Make a copy of these so we don't end up with duplicate labels
        IRTemp indexCopyTemp = new IRTemp(getFreshVariable());
        IRMove moveIndexCopyToTemp = new IRMove(indexCopyTemp, indexTemp);

        IRCJump cjump = new IRCJump( // index < length && index >= 0
                new IRBinOp(OpType.AND,
                        new IRBinOp(OpType.LT, indexTemp, length),
                        new IRBinOp(OpType.GEQ, indexCopyTemp, new IRConst(0))),
                trueLabel.name(),
                falseLabel.name()
        );

        // Make a copy of these so we don't end up with duplicate labels
        IRTemp arrayCopyTemp = new IRTemp(getFreshVariable());
        IRMove moveArrayCopyToTemp = new IRMove(arrayCopyTemp, arrTemp);

        IRTemp indexCopyCopyTemp = new IRTemp(getFreshVariable());
        IRMove moveIndexCopyCopyToTemp = new IRMove(indexCopyCopyTemp, indexTemp);

        // Get mem location
        IRSeq trueBody = new IRSeq(
                // get actual element
                new IRMove(result, new IRMem( new IRBinOp(
                        OpType.ADD,
                        new IRBinOp(OpType.MUL, indexCopyCopyTemp, new IRConst(Configuration.WORD_SIZE)),
                        arrayCopyTemp//arrayCopy
                ))),
                // Jump to exit
                new IRJump(new IRName(exitLabel.name()))
        );

        IRMove outOfBoundsCall = new IRMove(result, new IRCall(new IRName("_I_outOfBounds_p")));

        IRSeq seq = new IRSeq(
                moveArrToTemp,
                moveArrayCopyToTemp,
                moveIndexToTemp,
                moveIndexCopyToTemp,
                moveIndexCopyCopyToTemp,
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
        // TODO make first index of malloc's return IMMUTABLE IRMem

        // Save length in MEM(array)
        IRMove saveLength = new IRMove(new IRMem(new IRTemp(array)), new IRConst(length));
        // Shift array up to 0th index
        IRMove shift = new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD, new IRTemp(array), new IRConst(Configuration.WORD_SIZE)));

        stmts.add(storeArrayPtr);
        stmts.add(saveLength);
        stmts.add(shift);

        // Put elements into array spaces
        for (IRExpr e : arrayElements) {
            stmts.add(new IRMove(new IRMem(new IRTemp(array)), e)); // put array element in
            stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD, new IRTemp(array), new IRConst(Configuration.WORD_SIZE)))); // add 8 to array pointer
        }

        // Move array pointer back to index 0
        stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.SUB, new IRTemp(array), new IRConst(Configuration.WORD_SIZE * length))));

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
            if (typedDeclaration.getArraySizeList().size() > 0) {
                variable.accept(this);
                assert generatedNodes.peek() instanceof IRStmt;
                statements.add((IRStmt) generatedNodes.pop());
            }
            IRTemp temp = new IRTemp(typedDeclaration.getIdentifier().getName());
            IRMove move = new IRMove(temp, expr);
            statements.add(move);
            return new IRSeq(statements);
        } else if (variable instanceof ArrayIndex) {
            // If arrayindex on LHS, we need to handle out of bounds setting
            ((ArrayIndex) variable).getArrayRef().accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr array = (IRExpr)generatedNodes.pop();

            IRTemp arrTemp = new IRTemp(getFreshVariable());
            IRMove moveArrToTemp = new IRMove(arrTemp, array);

            ((ArrayIndex) variable).getIndex().accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr index = (IRExpr)generatedNodes.pop();

            IRTemp indexTemp = new IRTemp(getFreshVariable());
            IRMove moveIndexToTemp = new IRMove(indexTemp, index);

            IRMem length = new IRMem(new IRBinOp(OpType.SUB, arrTemp, new IRConst(Configuration.WORD_SIZE)));

            // Check for out of bounds index
            IRLabel trueLabel = new IRLabel(getFreshVariable());
            IRLabel falseLabel = new IRLabel(getFreshVariable());
            IRLabel exitLabel = new IRLabel(getFreshVariable());

            // Make a copy of these so we don't end up with duplicate labels
            IRTemp indexCopyTemp = new IRTemp(getFreshVariable());
            IRMove moveIndexCopyToTemp = new IRMove(indexCopyTemp, indexTemp);

            IRCJump cjump = new IRCJump( // index < length && index >= 0
                    new IRBinOp(OpType.AND,
                            new IRBinOp(OpType.LT, indexTemp, length),
                            new IRBinOp(OpType.GEQ, indexCopyTemp, new IRConst(0))),
                    trueLabel.name(),
                    falseLabel.name()
            );

            // Make a copy of these so we don't end up with duplicate labels
            IRTemp arrCopyTemp = new IRTemp(getFreshVariable());
            IRMove moveArrCopyToTemp = new IRMove(arrCopyTemp, arrTemp);

            IRTemp indexCopyCopyTemp = new IRTemp(getFreshVariable());
            IRMove moveIndexCopyCopyToTemp = new IRMove(indexCopyCopyTemp, indexCopyTemp);

            // move expr into location
            IRMem location = new IRMem(new IRBinOp(OpType.ADD,
                    arrCopyTemp,
                    new IRBinOp(OpType.MUL, indexCopyCopyTemp, new IRConst(Configuration.WORD_SIZE))
            ));
            IRSeq trueBody = new IRSeq(
                    // get actual element
                    new IRMove(location, expr),
                    // jump to exit
                    new IRJump(new IRName(exitLabel.name()))
            );

            IRExp outOfBoundsCall = new IRExp(new IRCall(new IRName("_I_outOfBounds_p")));

            IRSeq seq = new IRSeq(
                    moveArrToTemp,
                    moveArrCopyToTemp,
                    moveIndexToTemp,
                    moveIndexCopyToTemp,
                    moveIndexCopyCopyToTemp,
                    cjump,
                    trueLabel,
                    trueBody,
                    falseLabel,
                    outOfBoundsCall,
                    exitLabel
            );

            return seq;
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
        if (optype == OpType.ADD && ((VariableType)node.getLeft().getType()).getNumBrackets() > 0) {
            assert ((VariableType)node.getRight().getType()).getNumBrackets() > 0;
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
        List<Block> blockList = node.getBlockList();
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

    public void visit(CharacterLiteral node) {
        generatedNodes.push(new IRConst((long) node.getValue()));
    }

    public void visit(FunctionCall node) {
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
            List<Block> blocks = node.getBlockList().getBlockList();
            if (blocks.isEmpty() || !(blocks.get(blocks.size() - 1) instanceof ReturnStatement)) {
                blocks.add(new ReturnStatement(new LinkedList<>()));
            }
        }
        node.getBlockList().accept(this);

        assert generatedNodes.peek() instanceof IRSeq;
        IRSeq body = (IRSeq) generatedNodes.pop();
        List<IRStmt> fullBody = new ArrayList<>();
        for (int i = 0; i < node.getArgList().size(); i++) {
            IRTemp varTemp = new IRTemp(node.getArgList().get(i).getName());
            fullBody.add(new IRMove(varTemp, new IRTemp(Configuration.ABSTRACT_ARG_PREFIX + i)));
        }
        fullBody.addAll(body.stmts());

        IRFuncDecl irfd = new IRFuncDecl(Util.getIRFunctionName(node), new IRSeq(fullBody));
        generatedNodes.push(irfd);
    }

    public void visit(Identifier node) {
        // use the identifier as the name of the temp
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

    public void visit(ProcedureCall node) {
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

    public void visit(Program node) {
        Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
        // Go through all function declarations
        for (FunctionDeclaration fd : node.getFunctionDeclarationList()) {
            fd.accept(this);
            assert generatedNodes.peek() instanceof IRFuncDecl;
            functions.put(Util.getIRFunctionName(fd), (IRFuncDecl) generatedNodes.pop());
        }

        root = new IRCompUnit(name, functions);
        generatedNodes.push(root);
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

    public void visit(TypedDeclaration node) {
        if (node.getArraySizeList().size() == 0) {
            // If we don't need to initialize an array, do nothing
            generatedNodes.push(new IRExp(new IRConst(0)));
            return;
        }
        // In the case where TypedDeclaration is part of an assignment, it will be handled separately
        // We only concern with a standalone declaration here (x:int[], y:bool[4], z:int, etc)
        // We'll store the pointer to the initialized array in the temp of the variable name
        // else do nothing
        List<Expression> arraySizeList = node.getArraySizeList();
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
