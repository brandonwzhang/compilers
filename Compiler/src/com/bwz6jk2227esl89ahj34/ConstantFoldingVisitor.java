package com.bwz6jk2227esl89ahj34;

import com.bwz6jk2227esl89ahj34.AST.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jihunkim on 3/11/16.
 */
public class ConstantFoldingVisitor implements NodeVisitor {
    private List<Expression> lst;
    public ConstantFoldingVisitor() {
        lst = new LinkedList<>();
    }

    public void visit(ArrayIndex node) {
        node.getArrayRef().accept(this);
        node.getIndex().accept(this);
    }

    public void visit(ArrayLiteral node) {
        for(Expression e : node.getValues()) {
            e.accept(this);
        }
        node.setValues(new LinkedList<>(lst));
        lst = new LinkedList<>();

    }

    public void visit(Assignment node) {
        node.getExpression().accept(this);
        node.setExpression(lst.get(0));
        lst = new LinkedList<>();
    }

    public void visit(Binary node) {
        //TODO
        node.getLeft().accept(this);
        node.setLeft(lst.get(0));
        lst = new LinkedList<>();

        node.getRight().accept(this);
        node.setRight(lst.get(0));
        lst = new LinkedList<>();

        lst.add(node);
    }

    public void visit(BlockList node) {
        for(Block b : node.getBlockList()) {
            b.accept(this);
        }
    }

    public void visit(BooleanLiteral node) {
        lst.add(node);
    }

    public void visit(CharacterLiteral node) {
        lst.add(node);
    }

    public void visit(FunctionBlock node) {
        BlockList blockList = node.getBlockList();
        ReturnStatement returnStatement = node.getReturnStatement();
        blockList.accept(this);
        returnStatement.accept(this);
        returnStatement.setValues(new LinkedList<>(lst));
        lst = new LinkedList<>();
    }

    public void visit(FunctionCall node) {
        for(Expression e : node.getArguments()) {
            e.accept(this);
        }
        node.setArguments(new LinkedList<>(lst));
        lst = new LinkedList<>();
    }

    public void visit(FunctionDeclaration node) {
        MethodBlock methodBlock = node.getMethodBlock();
        methodBlock.accept(this);
    }

    public void visit(Identifier node) {
        lst.add(node);
    }

    public void visit(IfStatement node) {
        node.getGuard().accept(this);
        assert lst.size() == 1;
        node.setGuard(lst.get(0));
        lst = new LinkedList<>();
    }

    public void visit(IntegerLiteral node) {
        lst.add(node);
    }

    public void visit(ProcedureBlock node) {
        for(Block b : node.getBlockList().getBlockList()) {
            b.accept(this);
        }
    }

    public void visit(ProcedureCall node) {
        for(Expression e : node.getArguments()) {
            e.accept(this);
        }
        node.setArguments(new LinkedList<>(lst));
        lst = new LinkedList<>();
    }

    public void visit(Program node) {
        for (FunctionDeclaration functionDeclaration : node.getFunctionDeclarationList()) {
            functionDeclaration.accept(this);
        }
    }

    public void visit(ReturnStatement node) {
        List<Expression> returnValues = node.getValues();
        for (int i = 0; i < returnValues.size(); i++) {
            Expression expression = returnValues.get(i);
            expression.accept(this);
        }
        node.setValues(new LinkedList<>(lst));
        lst = new LinkedList<>();
    }

    public void visit(StringLiteral node) {
        lst.add(node);
    }

    public void visit(TypedDeclaration node) {
        for(Expression e : node.getArraySizeList()) {
            e.accept(this);
        }
        node.setArraySizeList(new LinkedList<>(lst));
        lst = new LinkedList<>();
    }

    public void visit(Unary node) {
        Expression temp = node;
        int count = 0;
        while(((Unary)temp).getExpression() instanceof Unary) {
            temp = ((Unary)temp).getExpression();
            count += 1;
        }
        if(count % 2 == 0) {
            lst.add(temp);
        } else {
            lst.add(new Unary(node.getOp(), temp));
        }
    }

    public void visit(Underscore node) {

    }

    public void visit(UseStatement node) {

    }

    public void visit(WhileStatement node) {
        node.getGuard().accept(this);
        assert lst.size() == 1;
        node.setGuard(lst.get(0));
        lst = new LinkedList<>();
    }
}
