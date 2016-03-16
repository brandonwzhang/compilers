package com.bwz6jk2227esl89ahj34;
import com.bwz6jk2227esl89ahj34.AST.*;
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
        return "" + (labelCounter++);
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

        IRMem location = new IRMem(new IRBinOp(IRBinOp.OpType.ADD, array, index)); // TODO double check

        generatedNodes.push(location);
    }

    public void visit(ArrayLiteral node) {
        // overall return type is IRMem

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

        IRTemp array = new IRTemp(getFreshVariable()); // temp to store array

        List<IRStmt> stmts = new LinkedList<>();
        // call to malloc
        IRCall malloc = new IRCall(new IRName("_I_alloc_i"), new IRConst(WORD_SIZE * (length + 1)));
        IRMove storeArrayPtr = new IRMove(array, malloc);
        // TODO make first index of malloc's return IMMUTABLE IRMem

        // save length in MEM(array)
        IRMove saveLength = new IRMove(new IRMem(array), new IRConst(length));
        // shift array up to 0th index
        IRMove shift = new IRMove(array, new IRBinOp(OpType.ADD, array, new IRConst(WORD_SIZE)));

        stmts.add(storeArrayPtr);
        stmts.add(saveLength);
        stmts.add(shift);

        // put elements into array spaces
        for (IRExpr e : arrayElements) {
            stmts.add(new IRMove(new IRMem(array), e)); // put array element in
            stmts.add(new IRMove(array, new IRBinOp(OpType.ADD, array, new IRConst(WORD_SIZE)))); // add 8 to array pointer
        }

        // move array pointer back to index 0
        stmts.add(new IRMove(array, new IRBinOp(OpType.ADD, array, new IRConst(WORD_SIZE * length))));

        IRSeq seq = new IRSeq(stmts);
        IRESeq eseq = new IRESeq(seq, new IRMem(array));

        generatedNodes.push(eseq);
    }

    public void visit(Assignment node) {
        List<Assignable> variables = node.getVariables();
        Expression expression = node.getExpression();
        // If RHS is VariableType, we know there was no function call
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
        // If RHS is VariableTypeList, we have to handle a function call
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
        String name = node.getIdentifier().getName();
        List<IRExpr> arguments = new ArrayList<>();
        // Store all arguments in a list
        for (Expression expression : node.getArguments()) {
            assert expression.getType() instanceof VariableType;
            expression.accept(this);
            assert generatedNodes.peek() instanceof IRExpr;
            IRExpr argument = (IRExpr) generatedNodes.pop();
            arguments.add(argument);
        }
        // Pass the function name and arguments to an IRCall
        IRCall call = new IRCall(new IRName(name), arguments);
        generatedNodes.push(call);
    }

    public void visit(FunctionDeclaration node) {
        String name = node.getIdentifier().getName();

        node.getMethodBlock().accept(this);
        assert generatedNodes.peek() instanceof IRStmt;
        IRStmt body = (IRStmt) generatedNodes.pop();

        assert body instanceof IRSeq;
        ((IRSeq) body).stmts().add(0, new IRLabel(name));

//        boolean isProcedure = false;
//        if (node.getFunctionType().getReturnTypeList().getVariableTypeList().size() == 0) {
//            isProcedure = true;
//        }

        IRFuncDecl irfd = new IRFuncDecl(name, body);
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
        assert generatedNodes.peek() instanceof IRExpr;
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
            assert generatedNodes.peek() instanceof IRExpr;
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

    }

    public void visit(Program node) {
        Map<String, IRFuncDecl> functions = new HashMap<>();
        // go through all function declarations
        for (FunctionDeclaration fd : node.getFunctionDeclarationList()) {
            fd.accept(this);
            assert generatedNodes.peek() instanceof IRFuncDecl;
            functions.put(fd.getIdentifier().getName(), (IRFuncDecl) generatedNodes.pop());
        }

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
    public void visit(TypedDeclaration node) {

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