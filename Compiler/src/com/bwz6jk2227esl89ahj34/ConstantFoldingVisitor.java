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
        node.getIndex().accept(this);
        assert lst.size() == 1;
        node.setIndex(lst.get(0));

        lst = new LinkedList<>();
        lst.add(node);
    }

    public void visit(ArrayLiteral node) {
        List<Expression> temp = new LinkedList<>();
        for(Expression e : node.getValues()) {
            e.accept(this);
            temp.add(lst.get(lst.size()-1));
        }
        node.setValues(temp);
        lst = new LinkedList<>();
        lst.add(node);

    }

    public void visit(Assignment node) {
        node.getExpression().accept(this);
        assert lst.size() == 1;
        node.setExpression(lst.get(0));
        lst = new LinkedList<>();
    }

    public void visit(Binary node) {
        //TODO
        node.getLeft().accept(this);
        node.setLeft(lst.get(lst.size()-1));
        System.out.println(lst.get(lst.size()-1));

        node.getRight().accept(this);
        node.setRight(lst.get(lst.size()-1));
        System.out.println(lst.get(lst.size()-1));
        lst = new LinkedList<>();
        lst.add(BinarySymbol.compute(node));
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
        Expression integerLiteral = new IntegerLiteral(""+(int)(node.getValue()));
        lst.add(integerLiteral);
    }

    public void visit(FunctionBlock node) {
        BlockList blockList = node.getBlockList();
        ReturnStatement returnStatement = node.getReturnStatement();
        blockList.accept(this);
        returnStatement.accept(this);
        lst = new LinkedList<>();
    }

    public void visit(FunctionCall node) {
        for(Expression e : node.getArguments()) {
            e.accept(this);
        }
        node.setArguments(new LinkedList<>(lst));
        lst = new LinkedList<>();
        lst.add(node);
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
        List<Expression> temp = new LinkedList<>();
        for (int i = 0; i < returnValues.size(); i++) {
            Expression expression = returnValues.get(i);
            expression.accept(this);
            temp.add(lst.get(0));
            lst = new LinkedList<>();
        }
        node.setValues(temp);
    }

    public void visit(StringLiteral node) {
        char[] str = node.getValue().toCharArray();
        List<Expression> expressions = new LinkedList<>();
        for (int i = 0; i < str.length; i++) {
            expressions.add(new IntegerLiteral(""+(int)(str[i])));
        }
        Expression arr = new ArrayLiteral(expressions);

        lst.add(arr);
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
        int count = 1;
        while(((Unary)temp).getExpression() instanceof Unary) {
            temp = ((Unary)temp).getExpression();
            count += 1;
        }
        if(count % 2 == 0) {
            Expression val = ((Unary)temp).getExpression();
            if(((IntegerLiteral)val).getValue().compareTo("9223372036854775808") > 0) {
                throw new TypeException("Number too big after constant folding performed");
            } else {
                lst.add(val);
            }
        } else {
            lst.add(temp);
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
