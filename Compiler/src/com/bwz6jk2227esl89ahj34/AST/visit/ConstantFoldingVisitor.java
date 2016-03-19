package com.bwz6jk2227esl89ahj34.AST.visit;

import com.bwz6jk2227esl89ahj34.AST.*;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

//TODO: remove the exceptions for over/underflow
public class ConstantFoldingVisitor implements NodeVisitor {
    private List<Expression> lst; //while the visitor visits, it will add to
                                 // or retrieve from the lst an appropriate
                                // expression depending on where they are in the
                               // AST

    public ConstantFoldingVisitor() {
        lst = new LinkedList<>();
    }

    /**
     * first visit the array ref (in case it's an array literal)
     * when it is visited, the list should contain the visited array ref
     * set the array ref as the visited array ref
     *
     * then, visit the expression which is the index of the array
     * when it is visited, the list should contain visited index; set the index
     * of the array to this
     *
     * if the array ref is an array literal and the index is an integer literal
     * then we can constant fold by extracting the element of the array
     *
     * we add this element to lst
     *
     * @param node
     */
    public void visit(ArrayIndex node) {
        node.getArrayRef().accept(this);
        //assert lst.size() == 1;
        //System.out.println(node.getArrayRef().getType());
        //lst.get(0).setType(node.getArrayRef().getType());
        //System.out.println(lst);
        node.setArrayRef(lst.get(lst.size()-1));
        //System.out.println(node.getArrayRef().getType());
        lst = new LinkedList<>();

        node.getIndex().accept(this);
        assert lst.size() == 1;
        node.setIndex(lst.get(0));
        lst = new LinkedList<>();
        if (node.getArrayRef() instanceof ArrayLiteral &&
                node.getIndex() instanceof IntegerLiteral) {
            int index = Integer.parseInt(((IntegerLiteral)(node.getIndex())).getValue());
            lst.add(((ArrayLiteral)(node.getArrayRef())).getValues().get(index));
        } else {
            lst.add(node);
        }
    }

    /**
     * visit each element of the array literal and add this to a temp list
     * then set the content of the array literal node to temp list
     * @param node
     */
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

    /**
     * for the variables of the assignment on the LHS, if the variable
     * is actually an index into an array, then visit it first. For all
     * variables, add it to newAssignable. Afterwards, visit the expression,
     * and set the expression to the new visited value
     * @param node
     */
    public void visit(Assignment node) {
        List<Assignable> assignables = node.getVariables();
        List<Assignable> newAssignables = new LinkedList<>();
        for (Assignable a : assignables) {
            if (a instanceof ArrayIndex) {
                a.accept(this);
                newAssignables.add((Assignable)(lst.get(0)));
                lst = new LinkedList<>();
            } else {
                newAssignables.add(a);
            }
        }
        node.setVariables(newAssignables);
        node.getExpression().accept(this);
        assert lst.size() == 1;
        node.setExpression(lst.get(0));
        lst = new LinkedList<>();
    }

    /**
     * visit the left expression first, and then set left to the value
     * yielded by the visit
     *
     * visit the right expression and do the same
     *
     * then, add the result of the computation of the binary expression
     * into lst
     * @param node
     */
    public void visit(Binary node) {
        node.getLeft().accept(this);
        node.setLeft(lst.get(lst.size()-1));

        node.getRight().accept(this);
        node.setRight(lst.get(lst.size()-1));
        lst = new LinkedList<>();
        lst.add(BinarySymbol.compute(node));
    }

    /**
     * for each block in the block list we visit
     * if the result of the visit of the block is an if statement and the
     * guard is a boolean literal then we can perform constant folding on it
     * if the boolean literal is true we only keep the true block, and that block
     * can actually stand alone without the if statement
     * otherwise we discard the if-statement & true block and the false block
     * can stand alone
     *
     * we also keep an eye out for a similar situation for the while loop, where
     * if the guard is true then the while loop is unnecessary and the block
     * stands alone, but if the guard is false then it just disappears
     *
     * as we process the blocks we add it to a blockList list and then
     * set the blocklist of the node to the blockList we were constructing
     * @param node
     */
    public void visit(BlockList node) {
        List<Block> blockList = new LinkedList<>();
        for(Block b : node.getBlockList()) {
            b.accept(this);
            if (b instanceof IfStatement &&
                    ((IfStatement)b).getGuard() instanceof BooleanLiteral) {
                boolean bool = ((BooleanLiteral)(((IfStatement)b).getGuard())).getValue();
                if (bool) {
                    blockList.add(((IfStatement)(b)).getTrueBlock());
                } else {
                    Optional<Block> fb = ((IfStatement)(b)).getFalseBlock();
                    if (!fb.equals(Optional.empty())) {
                        blockList.add(fb.get());
                    }
                }
            } else if (b instanceof WhileStatement &&
                    ((WhileStatement)b).getGuard() instanceof BooleanLiteral) {
                boolean bool =  ((BooleanLiteral)((WhileStatement)b).getGuard()).getValue();
                if (bool) {
                    blockList.add(((WhileStatement)b).getBlock());
                }
                //else we don't add anything so nothing to do
            } else {
                blockList.add(b);
            }
        }
        node.setBlockList(new LinkedList<>(blockList));
    }

    /**
     * add the boolean literal into the list
     * @param node
     */
    public void visit(BooleanLiteral node) {
        lst.add(node);
    }

    /**
     * cast the character literal into its corresponding int value,
     * and add the int value to the list
     * @param node
     */
    public void visit(CharacterLiteral node) {
        Expression integerLiteral = new IntegerLiteral(""+(int)(node.getValue()));
        lst.add(integerLiteral);
    }

    /**
     * for the function block, we first visit the block list
     * that is contained in the function block
     *
     * then we visit the return statement
     *
     * afterwards we empty the lst
     * @param node
     */
    public void visit(FunctionBlock node) {
        BlockList blockList = node.getBlockList();
        ReturnStatement returnStatement = node.getReturnStatement();
        blockList.accept(this);
        returnStatement.accept(this);
        lst = new LinkedList<>();
    }

    /**
     * we simplify the arguments to the function call and add them to a
     * newArguments list
     *
     * then we set newArguments to the arguments of node
     *
     * afterwards, we add the node to lst
     * @param node
     */
    public void visit(FunctionCall node) {
        List<Expression> newArguments = new LinkedList<>();
        for(Expression e : node.getArguments()) {
            e.accept(this);
            newArguments.add(lst.get(lst.size()-1));
            lst = new LinkedList<>();
        }
        node.setArguments(new LinkedList<>(newArguments));
        lst = new LinkedList<>();
        lst.add(node);
    }

    /**
     * visit the methodblock
     * @param node
     */
    public void visit(FunctionDeclaration node) {
        MethodBlock methodBlock = node.getMethodBlock();
        methodBlock.accept(this);
    }

    /**
     * add the Identifier to lst
     * @param node
     */
    public void visit(Identifier node) {
        lst.add(node);
    }

    /**
     * visit the guard, then set the guard to the visited guard
     * then accept the true block
     * if the false block is also present, visit it as well
     * @param node
     */
    public void visit(IfStatement node) {
        node.getGuard().accept(this);
        assert lst.size() == 1;
        node.setGuard(lst.get(0));
        lst = new LinkedList<>();
        node.getTrueBlock().accept(this);
        if (node.getFalseBlock().isPresent()) {
            node.getFalseBlock().get().accept(this);
        }
    }

    /**
     * add integer literal to lst
     * @param node
     */
    public void visit(IntegerLiteral node) {
        lst.add(node);
    }

    /**
     * visit the blocklist of procedure block
     * @param node
     */
    public void visit(ProcedureBlock node) {
        node.getBlockList().accept(this);
    }

    /**
     * visit every argument to the procedure call
     * and add each visited argument to a newArguments list
     *
     * afterwards, set the arguments of procedure call
     * @param node
     */
    public void visit(ProcedureCall node) {
        List<Expression> newArguments = new LinkedList<>();
        for(Expression e : node.getArguments()) {
            e.accept(this);
            newArguments.add(lst.get(0));
            lst = new LinkedList<>();
        }
        node.setArguments(new LinkedList<>(newArguments));
        lst = new LinkedList<>();
    }

    /**
     * for each function declaration in the program, we constant fold
     * note that we do not care about use statements for good reason :P
     * @param node
     */
    public void visit(Program node) {
        for (FunctionDeclaration functionDeclaration :
                node.getFunctionDeclarationList()) {
            functionDeclaration.accept(this);
        }
    }

    /**
     * for each expression of the return statement, we visit it
     * and add the visited value to a temp list
     *
     * then we set the values of the return statement to temp
     * @param node
     */
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

    /**
     * we convert the string literal to an int array
     * and add the array into lst
     * @param node
     */
    public void visit(StringLiteral node) {
        char[] str = node.getValue().toCharArray();
        List<Expression> expressions = new LinkedList<>();
        for (int i = 0; i < str.length; i++) {
            expressions.add(new IntegerLiteral(""+(int)(str[i])));
        }
        Expression arr = new ArrayLiteral(expressions);

        lst.add(arr);
    }

    /**
     * for a typed declaration, if the declaration involves a fixed size
     * arrays, then we visit each expression that specifies the size of
     * the array
     * @param node
     */
    public void visit(TypedDeclaration node) {
        List<Expression> newArraySizeList = new LinkedList<>();
        for(Expression e : node.getArraySizeList()) {
            e.accept(this);
            newArraySizeList.add(lst.get(0));
            lst = new LinkedList<>();
        }
        node.setArraySizeList(newArraySizeList);
        lst = new LinkedList<>();
    }

    /**
     * for a Unary node, we count how many times the unary operation
     * occurs and reduce it accordingly by the count and type of the expression
     * wrapped by the Unary
     * @param node
     */
    public void visit(Unary node) {
        Expression temp = node;
        int count = 1;
        while(((Unary)temp).getExpression() instanceof Unary) {
            temp = ((Unary)temp).getExpression();
            count += 1;
        }
        Expression val = ((Unary)temp).getExpression();
        val.accept(this);
        if(count % 2 == 0) {
            if(val instanceof IntegerLiteral &&
                    ((IntegerLiteral)val).getValue()
                            .compareTo("9223372036854775808") >= 0) {
                BigInteger bi = new BigInteger(((IntegerLiteral)val).getValue());
                lst = new LinkedList<>();
                lst.add(new IntegerLiteral(""+bi.longValue()));
                //throw new TypeException("Number too big after constant folding performed");
            } else {
                //lst.add(val); val is already added so we don't need it
            }
        } else {
            if(val instanceof IntegerLiteral) {
                BigInteger longValue = new BigInteger(((IntegerLiteral)(lst.get(0))).getValue());
                lst = new LinkedList<>();
                longValue = longValue.multiply(new BigInteger("-1"));
                lst.add(new IntegerLiteral(""+longValue.longValue()));
            } else if (val instanceof BooleanLiteral){
                lst = new LinkedList<>();
                lst.add(new BooleanLiteral(false));
            } else {
                lst = new LinkedList<>();
                lst.add(new Unary(node.getOp(), val));
            }

        }
    }

    /**
     * nothing to do for underscore
     * @param node
     */
    public void visit(Underscore node) {

    }

    /**
     * nothing to do for use statement
     * @param node
     */
    public void visit(UseStatement node) {

    }

    /**
     * for while statements we visit its guard
     * and then set the guard to the visited guard
     * then we visit the block
     * @param node
     */
    public void visit(WhileStatement node) {
        node.getGuard().accept(this);
        assert lst.size() == 1;
        node.setGuard(lst.get(0));
        lst = new LinkedList<>();
        node.getBlock().accept(this);
    }
}
