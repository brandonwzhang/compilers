package com.bwz6jk2227esl89ahj34.AST.visit;
import com.bwz6jk2227esl89ahj34.AST.*;
import com.bwz6jk2227esl89ahj34.AST.type.VariableType;
import com.bwz6jk2227esl89ahj34.AST.type.VariableTypeList;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.ir.IRBinOp.OpType;

import java.util.*;

public class MIRGenerateVisitor implements NodeVisitor {
    // Word is 8 bytes
    public static final int WORD_SIZE = 8;

    // The root of the MIR Tree
    private IRCompUnit IRRoot;
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

    public IRCompUnit getIRRoot() {
        return IRRoot;
    }

    private String getFreshVariable() {
        return "temp" + (labelCounter++);
    }

    private IRExpr makeCopy(IRExpr e) {
        if (e instanceof IRBinOp) {
            return new IRBinOp((IRBinOp)e);
        }
        if (e instanceof IRCall) {
            return new IRCall((IRCall)e);
        }
        if (e instanceof IRConst) {
            return new IRConst((IRConst)e);
        }
        if (e instanceof IRESeq) {
            return new IRESeq((IRESeq)e);
        }
        if (e instanceof IRMem) {
            return new IRMem((IRMem)e);
        }
        if (e instanceof IRName) {
            return new IRName((IRName)e);
        }
        if (e instanceof IRTemp) {
            return new IRTemp((IRTemp)e);
        }
        return null; // should not be reached
    }
    private IRStmt makeCopy(IRStmt s) {
        if (s instanceof IRCJump) {
            return new IRCJump((IRCJump)s);
        }
        if (s instanceof IRExp) {
            return new IRExp((IRExp)s);
        }
        if (s instanceof IRJump) {
            return new IRJump((IRJump)s);
        }
        if (s instanceof IRLabel) {
            return new IRLabel((IRLabel)s);
        }
        if (s instanceof IRMove) {
            return new IRMove((IRMove)s);
        }
        if (s instanceof IRSeq) {
            return new IRSeq((IRSeq)s);
        }
        return null; // should not be reached
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

        node.getArrayRef().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr array = (IRExpr)generatedNodes.pop();

        node.getIndex().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr index = (IRExpr)generatedNodes.pop();

        IRMem location = new IRMem(new IRBinOp(OpType.ADD, makeCopy(array),
                new IRBinOp(OpType.MUL, index, new IRConst(WORD_SIZE)))); // TODO double check

        generatedNodes.push(location);
    }

    public void visit(ArrayLiteral node) {
        // overall return type is IRTemp t, of which IRMem(t) is index 0 of array

        // eseq
        // call malloc for 8(n+1), the size of the arrayliteral, move result into a
        // move n (length of arrayliteral) into a
        // move a+8 into a
        // return a

        List<IRExpr> arrayElements = new LinkedList<IRExpr>();
        int length = node.getValues().size();
        for (Expression e : node.getValues()) {
            e.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            arrayElements.add((IRExpr)generatedNodes.pop());
        }

        String array = getFreshVariable(); // temp to store array

        List<IRStmt> stmts = new LinkedList<>();
        // call to malloc
        IRCall malloc = new IRCall(new IRName("_I_alloc_i"), new IRConst(WORD_SIZE * (length + 1)));
        IRMove storeArrayPtr = new IRMove(new IRTemp(array), malloc);
        // TODO make first index of malloc's return IMMUTABLE IRMem

        // save length in MEM(array)
        IRMove saveLength = new IRMove(new IRMem(new IRTemp(array)), new IRConst(length));
        // shift array up to 0th index
        IRMove shift = new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD, new IRTemp(array), new IRConst(WORD_SIZE)));

        stmts.add(storeArrayPtr);
        stmts.add(saveLength);
        stmts.add(shift);

        // put elements into array spaces
        for (IRExpr e : arrayElements) {
            stmts.add(new IRMove(new IRMem(new IRTemp(array)), e)); // put array element in
            stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.ADD, new IRTemp(array), new IRConst(WORD_SIZE)))); // add 8 to array pointer
        }

        // move array pointer back to index 0
        stmts.add(new IRMove(new IRTemp(array), new IRBinOp(OpType.SUB, new IRTemp(array), new IRConst(WORD_SIZE * length))));

        IRSeq seq = new IRSeq(stmts);
        IRESeq eseq = new IRESeq(seq, new IRTemp(array));

        generatedNodes.push(eseq);
    }

    public void visit(Assignment node) {
        List<Assignable> variables = node.getVariables();
        Expression expression = node.getExpression();
        // If RHS is VariableType, we know there was no multiassign
        if (expression.getType() instanceof VariableType) {
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr evaluatedExpression = (IRExpr) generatedNodes.pop();

            assert variables.size() == 1;
            Assignable variable = variables.get(0);
            if (variable instanceof Underscore) {
                // Throw away if underscore
                return;
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
                IRMove move = new IRMove(temp, evaluatedExpression);
                statements.add(move);
                generatedNodes.push(new IRSeq(statements));
                return;
            }
            variable.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRMove move = new IRMove((IRExpr) generatedNodes.pop(), evaluatedExpression);
            generatedNodes.push(move);
            return;
        }
        // If RHS is VariableTypeList, we have to handle a multiassign
        assert expression instanceof FunctionCall;
        assert expression.getType() instanceof VariableTypeList;
        assert variables.size() == ((VariableTypeList) expression.getType()).getVariableTypeList().size();
        List<IRStmt> statements = new ArrayList<>();
        expression.accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        // Add the function call to the IRSeq
        // Throw away the evaluated expression since we'll be getting it from the return registers
        statements.add(new IRExp((IRExpr) generatedNodes.pop()));

        for (int i = 0; i < variables.size(); i++) {
            Assignable variable = variables.get(i);
            if (variable instanceof Underscore) {
                // Throw away return value if underscore
                continue;
            } else if (variable instanceof TypedDeclaration) {
                // Since TypedDeclarations are treated as statements, we must
                // handle them separately
                TypedDeclaration typedDeclaration = (TypedDeclaration) variable;
                if (typedDeclaration.getArraySizeList().size() > 0) {
                    variable.accept(this);
                    assert generatedNodes.peek() instanceof IRStmt;
                    statements.add((IRStmt) generatedNodes.pop());
                }
                IRTemp temp = new IRTemp(typedDeclaration.getIdentifier().getName());
                IRMove move = new IRMove(temp, new IRTemp("_RET" + i));
                statements.add(move);
                continue;
            }
            // We know LHS must be either identifier or array index at this point
            variable.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRMove move = new IRMove((IRExpr) generatedNodes.pop(), new IRTemp("_RET" + i));
            statements.add(move);
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

        node.getRight().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        right = (IRExpr)generatedNodes.pop();

        // array addition case
        if (optype == OpType.ADD && ((VariableType)node.getLeft().getType()).getNumBrackets() > 0) {
            assert ((VariableType)node.getRight().getType()).getNumBrackets() > 0;
            // get length of operands
            IRMem leftLength = new IRMem(new IRBinOp(OpType.SUB, left, new IRConst(WORD_SIZE)));
            IRMem rightLength = new IRMem(new IRBinOp(OpType.SUB, right, new IRConst(WORD_SIZE)));
            IRBinOp combinedLength = new IRBinOp(OpType.ADD, leftLength, rightLength);

            /* Allocate space for new array */
            String combinedArray = getFreshVariable();
            List<IRStmt> stmts = new LinkedList<>();
            // call malloc
            IRCall malloc = new IRCall(new IRName("_I_alloc_i"),
                    new IRBinOp(OpType.MUL,
                            new IRBinOp(OpType.ADD, combinedLength, new IRConst(1)),
                            new IRConst(WORD_SIZE)));
            IRMove storeArrayPtr = new IRMove(new IRTemp(combinedArray), malloc);
            // TODO make first index of malloc's return IMMUTABLE IRMem

            // save length in MEM(combinedArray)
            IRMove saveLength = new IRMove(new IRMem(new IRTemp(combinedArray)), combinedLength);
            // shift array up to 0th index
            IRMove shift = new IRMove(new IRTemp(combinedArray), new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(WORD_SIZE)));

            stmts.add(storeArrayPtr);
            stmts.add(saveLength);
            stmts.add(shift);

            /* Insert elements into new array */
            // insert elements from left array
            String index = getFreshVariable();
            stmts.add(new IRMove(new IRTemp(index), new IRConst(0)));

            IRLabel headLabel = new IRLabel(getFreshVariable());
            IRLabel trueLabel = new IRLabel(getFreshVariable());
            IRLabel falseLabel = new IRLabel(getFreshVariable());

            IRBinOp guard = new IRBinOp(OpType.LT, new IRTemp(index), combinedLength);
            IRCJump cjump = new IRCJump(guard, trueLabel.name(), falseLabel.name());

            IRBinOp leftElement = new IRBinOp(OpType.ADD,
                    new IRBinOp(OpType.MUL,
                            new IRTemp(index), new IRConst(WORD_SIZE)),
                    left);
            IRMove trueMove1 = new IRMove(new IRMem(new IRTemp(combinedArray)), new IRMem(leftElement));
            IRMove trueMove2 = new IRMove(new IRTemp(combinedArray),
                    new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(WORD_SIZE)));
            IRJump headJump = new IRJump(new IRName(headLabel.name()));

            stmts.add(headLabel);
            stmts.add(cjump);
            stmts.add(trueLabel);
            stmts.add(trueMove1);
            stmts.add(trueMove2);
            stmts.add(headJump);
            stmts.add(falseLabel);

            // insert elements from right array
            stmts.add(new IRMove(new IRTemp(index), new IRConst(0))); // reset index to 0

            IRLabel headLabel2 = new IRLabel(getFreshVariable());
            IRLabel trueLabel2 = new IRLabel(getFreshVariable());
            IRLabel falseLabel2 = new IRLabel(getFreshVariable());

            IRBinOp guard2 = new IRBinOp(OpType.LT, new IRTemp(index), combinedLength);
            IRCJump cjump2 = new IRCJump(guard2, trueLabel2.name(), falseLabel2.name());

            IRBinOp rightElement = new IRBinOp(OpType.ADD,
                    new IRBinOp(OpType.MUL,
                            new IRTemp(index), new IRConst(WORD_SIZE)),
                    right);
            IRMove trueMove1_ = new IRMove(new IRMem(new IRTemp(combinedArray)), new IRMem(rightElement));
            IRMove trueMove2_ = new IRMove(new IRTemp(combinedArray),
                    new IRBinOp(OpType.ADD, new IRTemp(combinedArray), new IRConst(WORD_SIZE)));
            IRJump headJump2 = new IRJump(new IRName(headLabel2.name()));

            stmts.add(headLabel2);
            stmts.add(cjump2);
            stmts.add(trueLabel2);
            stmts.add(trueMove1_);
            stmts.add(trueMove2_);
            stmts.add(headJump2);
            stmts.add(falseLabel2);

            /* return new combinedArray to index 0 address */
            stmts.add(new IRMove(new IRTemp(combinedArray),
                    new IRBinOp(OpType.SUB,
                            new IRTemp(combinedArray),
                            new IRBinOp(OpType.SUB,
                                    new IRBinOp(OpType.MUL,
                                            new IRConst(WORD_SIZE), combinedLength),
                                    new IRConst(1))
                    ))
            );

            IRSeq seq = new IRSeq(stmts);
            IRESeq eseq = new IRESeq(seq, new IRTemp(combinedArray));

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

            IRSeq seq = new IRSeq(Arrays.asList(initmove, cjump, continuelabel, truemove, skiplabel));
            IRESeq eseq = new IRESeq(seq, result);
            generatedNodes.push(eseq);
            return;
        }
        if (optype == OpType.OR) {
            // same as above, but with left evaluating to true (and a default result of 1 (true)
            IRTemp result = new IRTemp(getFreshVariable());
            IRLabel continuelabel = new IRLabel(getFreshVariable());
            IRLabel skiplabel = new IRLabel(getFreshVariable());
            IRCJump cjump = new IRCJump(left, skiplabel.name(), continuelabel.name());
            IRMove initmove = new IRMove(result, new IRConst(1));
            IRMove truemove = new IRMove(result, right);

            IRSeq seq = new IRSeq(Arrays.asList(initmove, cjump, continuelabel, truemove, skiplabel));
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

    // not done
    public void visit(FunctionBlock node) {
        List<Block> blockList = node.getBlockList().getBlockList();
        ReturnStatement returnStatement = node.getReturnStatement();
        List<Expression> returnValues = returnStatement.getValues();
        List<IRStmt> stmtList = new ArrayList<>();

        for (Block block : blockList) {
            block.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            stmtList.add((IRStmt) generatedNodes.pop());
        }

        for (int i = 0; i < returnValues.size(); i++) {
            returnValues.get(i).accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr expr = (IRExpr) generatedNodes.pop();
            IRExpr target = new IRTemp("_RET" + i);
            stmtList.add(new IRMove(target, expr));
        }

        stmtList.add(new IRReturn());
        IRStmt body = new IRSeq(stmtList);
        generatedNodes.push(body);
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

        String irFunctionName = "";
        if (node.getType() instanceof VariableTypeList) {
            FunctionType funcType = new FunctionType(argTypeList, (VariableTypeList) node.getType());
            FunctionDeclaration tempFuncDec = new FunctionDeclaration(node.getIdentifier(), funcType, null, null);
            irFunctionName = getIRFunctionName(tempFuncDec);
        } else {
            assert node.getType() instanceof VariableType;
            List<VariableType> retTypes = new ArrayList<>(Arrays.asList((VariableType) node.getType()));
            FunctionType funcType = new FunctionType(argTypeList, new VariableTypeList(retTypes));
            FunctionDeclaration tempFuncDec = new FunctionDeclaration(node.getIdentifier(), funcType, null, null);
            irFunctionName = getIRFunctionName(tempFuncDec);
        }


        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(new IRName(irFunctionName), arguments);
        generatedNodes.push(call);
    }

    public static String getTypeString(VariableType type) {
        String typeString = "";
        switch (type.getPrimitiveType()) {
            case BOOL:
                typeString = "b";
                break;
            case INT:
                typeString = "i";
                break;
            default:
                throw new RuntimeException("Invalid type");
        }
        for (int i = 0; i < type.getNumBrackets(); i++) {
            typeString = "a" + typeString;
        }
        return typeString;
    }

    public String getIRFunctionName(FunctionDeclaration node) {
        String funcName = node.getIdentifier().getName();
        FunctionType funcType = node.getFunctionType();
        String irName = "_I" + funcName + "_";

        String ret = "";
        List<VariableType> retList = funcType.getReturnTypeList().getVariableTypeList();
        if (retList.size() > 1) {
            ret = "t" + retList.size();
        } else if (retList.size() == 0) {
            ret = "p";
        }
        for (VariableType type : retList) {
            ret += getTypeString(type);
        }

        String arg = "";
        List<VariableType> argList = funcType.getArgTypeList();
        for (VariableType type : argList) {
            arg += getTypeString(type);
        }
        irName += ret + arg;
        return irName;
    }

    public void visit(FunctionDeclaration node) {
        node.getMethodBlock().accept(this);
        assert generatedNodes.peek() instanceof IRStmt;
        IRStmt body = (IRStmt) generatedNodes.pop();

        IRFuncDecl irfd = new IRFuncDecl(getIRFunctionName(node), body);
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
            statements.add(new IRJump(new IRName(endLabelName)));
            statements.add(falseLabel);
            node.getFalseBlock().get().accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            IRStmt falseBlock = (IRStmt) generatedNodes.pop();
            statements.add(falseBlock);
            statements.add(new IRJump(new IRName(endLabelName)));
        } else {
            IRCJump cjump = new IRCJump(guard, trueLabelName);
            statements.add(cjump);
        }

        statements.add(trueLabel);
        statements.add(trueBlock);
        statements.add(endLabel);
        generatedNodes.push(new IRSeq(statements));
    }

    public void visit(IntegerLiteral node) {
        long value = Long.parseLong(node.getValue());
        generatedNodes.push(new IRConst(value));
    }

    public void visit(ProcedureBlock node) {
        List<Block> blockList = node.getBlockList().getBlockList();
        List<IRStmt> stmtList = new ArrayList<>();

        for (Block block : blockList) {
            block.accept(this);
            assert generatedNodes.peek() instanceof IRStmt;
            stmtList.add((IRStmt) generatedNodes.pop());
        }

        stmtList.add(new IRReturn());
        IRStmt body = new IRSeq(stmtList);
        generatedNodes.push(body);
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
        IRCall call = new IRCall(new IRName(getIRFunctionName(tempFuncDec)), arguments);
        // We need to throw out the return value;
        generatedNodes.push(new IRExp(call));
    }

    public void visit(Program node) {
        Map<String, IRFuncDecl> functions = new HashMap<>();
        // go through all function declarations
        for (FunctionDeclaration fd : node.getFunctionDeclarationList()) {
            fd.accept(this);
            assert generatedNodes.peek() instanceof IRFuncDecl;
            functions.put(getIRFunctionName(fd), (IRFuncDecl) generatedNodes.pop());
        }

        // add length builtin function TODO
        List<IRStmt> lengthBody = new LinkedList<>();

        // add ArrayOutofBounds function (throws OOB error) TODO

        IRRoot = new IRCompUnit(name, functions);
        generatedNodes.push(IRRoot);
    }

    public void visit(ReturnStatement node) {

    }
    public void visit(StringLiteral node) {
        // StringLiteral = int ArrayLiteral
        char[] array = node.getValue().toCharArray();
        List<Expression> chars = new LinkedList<>();
        for (char c : array) {
            chars.add(new CharacterLiteral(c));
        }

        ArrayLiteral arrayliteral = new ArrayLiteral(chars);
        arrayliteral.accept(this);
    }
    public IRBinOp scaleByWordSize(IRExpr expr) {
        return new IRBinOp(OpType.MUL, expr, new IRConst(WORD_SIZE));
    }

    public IRSeq initializeArray(IRTemp parentArrayPointer, IRExpr parentArrayIndex, List<IRExpr> lengths, int index) {
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
        statements.add(new IRMove(arrayTemp, new IRBinOp(OpType.ADD, arrayTemp, new IRConst(WORD_SIZE))));
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
            return new IRSeq(statements);
        }
        // Iterate through the array and recursively allocate memory for subarrays
        IRLabel headLabel = new IRLabel(getFreshVariable());
        IRLabel trueLabel = new IRLabel(getFreshVariable());
        IRLabel exitLabel = new IRLabel(getFreshVariable());
        IRTemp counter = new IRTemp(getFreshVariable());
        statements.add(new IRMove(counter, new IRConst(0)));
        IRExpr guard = new IRBinOp(OpType.LT, counter, length);
        IRSeq allocateSubarrays = initializeArray(arrayTemp, counter, lengths, index + 1);
        List<IRStmt> loopStatements = new ArrayList<>();
        loopStatements.add(headLabel);
        loopStatements.add(new IRCJump(guard, trueLabel.name(), exitLabel.name()));
        loopStatements.add(trueLabel);
        loopStatements.add(allocateSubarrays);
        loopStatements.add(new IRMove(counter, new IRBinOp(OpType.ADD, counter, new IRConst(1))));
        loopStatements.add(new IRJump(new IRName(headLabel.name())));
        loopStatements.add(exitLabel);
        statements.add(new IRSeq(loopStatements));
        return new IRSeq(statements);
    }

    public void visit(TypedDeclaration node) {
        // In the case where TypedDeclaration is part of an assignment, it will be handled separately
        // We only concern with a standalone declaration here (x:int[], y:bool[4], z:int, etc)
        // We'll store the pointer to the initialized array in the temp of the variable name
        // else do nothing

        List<Expression> arraySizeList = node.getArraySizeList();
        List<IRExpr> lengths = new ArrayList<>();
        for (Expression arraySize : arraySizeList) {
            arraySize.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            lengths.add((IRExpr) generatedNodes.pop());
        }
        while (lengths.size() < node.getDeclarationType().getNumBrackets()) {
            lengths.add(new IRConst(0));
        }
        String variableName = node.getIdentifier().getName();
        IRTemp array = new IRTemp(variableName);
        generatedNodes.push(new IRSeq(initializeArray(array, null, lengths, 0)));
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
        node.getExpression().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        right = (IRExpr)generatedNodes.pop();

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
    public void visit(Underscore node) {
        // Handled in Assignment
        return;
    }
    public void visit(UseStatement node) {
        return;
    }
    public void visit(WhileStatement node) {
        // create labels
        String headLabelName = getFreshVariable();
        IRLabel headLabel = new IRLabel(headLabelName);
        String trueLabelName = getFreshVariable();
        IRLabel trueLabel = new IRLabel(trueLabelName);
        String exitLabelName = getFreshVariable();
        IRLabel exitLabel = new IRLabel(exitLabelName);

        // perform translations
        node.getGuard().accept(this);
        assert generatedNodes.peek() instanceof IRExpr;
        IRExpr guard = (IRExpr)generatedNodes.pop();

        node.getBlock().accept(this);
        assert generatedNodes.peek() instanceof IRStmt;
        IRStmt body = (IRStmt)generatedNodes.pop();

        IRSeq seq = new IRSeq(
                headLabel,
                new IRCJump(guard, trueLabelName, exitLabelName),
                trueLabel,
                body,
                new IRJump(new IRName(headLabelName)),
                exitLabel);

        generatedNodes.push(seq);
    }
}