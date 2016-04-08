package com.bwz6jk2227esl89ahj34.AST.visit;

import com.bwz6jk2227esl89ahj34.AST.*;
import com.bwz6jk2227esl89ahj34.util.Util;

import java.math.BigInteger;
import java.util.*;

//TODO: remove the exceptions for over/underflow
public class ConstantFoldingVisitor implements NodeVisitor {
    private Stack<Expression> stack; //while the visitor visits, it will push to
                                 // or pop from the stack an appropriate
                                // expression depending on where they are in the
                               // AST
    private Stack<Assignable> assignableStack; // while the visitor visits, it
                                              // will push or pop to this stack
                                     // for constant folding LHS of assignments

    boolean LHS = false; // for taking care of array indexing, which
                               // can happen at the LHS or RHS of an assignment


    public ConstantFoldingVisitor() {
        stack = new Stack<>();
        assignableStack = new Stack<>();
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
     * we add this element to the appropriate stack
     *
     * @param node
     */
    public void visit(ArrayIndex node) {
        // we first see if the array reference can be constant folded ...
        node.getArrayRef().accept(this);
        // and set the array ref to the potentially constant folded array ref
        if(LHS) {
            assert !assignableStack.isEmpty();
            node.setArrayRef((Expression)(assignableStack.pop()));
        } else {
            assert !stack.isEmpty();
            node.setArrayRef(stack.pop());
        }


        // then we perform constant folding on the index
        node.getIndex().accept(this);
        // and set the array index to the potentially constant folded index

        if(LHS && ! (node.getIndex() instanceof Binary)
                && !(node.getIndex() instanceof IntegerLiteral)
                && !(node.getIndex() instanceof CharacterLiteral)) {
            assert !assignableStack.isEmpty();
            node.setIndex((Expression)(assignableStack.pop()));
        } else {
            assert !stack.isEmpty();
            node.setIndex(stack.pop());
        }

        // if the array ref is an array literal and the index is an
        // integer literal then that means this node can be constant
        // folded in a way that we extract the element in the index of
        // interest in the array
        if (node.getArrayRef() instanceof ArrayLiteral &&
                node.getIndex() instanceof IntegerLiteral) {
            // because lists in java can only handle up to an int-bounded
            // number of elements, we convert the index to an int
            // rather than a long
            int index = Integer.parseInt(((IntegerLiteral)(node.getIndex())).getValue());
            stack.push(((ArrayLiteral)(node.getArrayRef())).getValues().get(index));
        } else {
            // otherwise, we just push the constant folded node onto the stack

            // however, if this came from the LHS of an assignment, we
            // push the node onto the assingable stack
            if(LHS) {
             assignableStack.push(node);
            } else { // otherwise we push it onto the regular stack
                stack.push(node);
            }
        }
    }

    /**
     * visit each element of the array literal and add this to a temp list
     * then set the content of the array literal node to temp list
     * @param node
     */
    public void visit(ArrayLiteral node) {
        // we visit every element of the array, applying the visitor paradigm
        // to each element so that each element will be constant folded
        // and add the constant folded value onto a temp list
        List<Expression> temp = new LinkedList<>();
        for(Expression e : node.getValues()) {
            e.accept(this);
            assert !stack.isEmpty(); // something is on the stack at this point
            temp.add(stack.pop());
        }
        // after setting the value of node to temp, we push it onto
        // the stack so it's available on the stack
        node.setValues(temp);
        stack.push(node);
    }

    /**
     * for the variables of the assignment on the LHS, if the variable
     * is actually an index into an array, then visit it first. For all
     * variables, add it to newAssignable. Afterwards, visit the expression,
     * and set the expression to the new visited value
     * @param node
     */
    public void visit(Assignment node) {
        // for each variable we are assigning (element of assingables)
        // we perform constant folding over it and add it to newAssignables
        // aka we are applying constant folding on the LHS of assignment
        List<Assignable> assignables = node.getVariables();
        List<Assignable> newAssignables = new LinkedList<>();

        // to notify everyone that we are constant folding on the LHS
        // of an assignment...
        LHS = true;

        for (Assignable a : assignables) {
            a.accept(this);
            assert !assignableStack.isEmpty();
            newAssignables.add(assignableStack.pop());
        }
        // then we set the variables to to the list of constant folded
        // variables in newAssignables
        node.setVariables(newAssignables);

        // to notify everyone that we are no longer constant folding
        // on the LHS of an assignment...
        LHS = false;

        // then we apply constant folding on the RHS of the assignment
        node.getExpression().accept(this);
        // and set the constant folded RHS as the RHS of the assignment
        assert !stack.isEmpty();
        node.setExpression(stack.pop());

        // an assignment itself cannot be constant folded so we do not
        // add it to the stack
    }

    /**
     * visit the left expression first, and then set left to the value
     * yielded by the visit
     *
     * visit the right expression and do the same
     *
     * then, add the result of the computation of the binary expression
     * into a stack
     * @param node
     */
    public void visit(Binary node) {
        // we accept the left expression of the binary node
        node.getLeft().accept(this);
        // and set its left to its constant-folded form that should be
        // available on the stack
        if (node.getLeft() instanceof Identifier && LHS) {
            assert !assignableStack.isEmpty();
            node.setLeft((Expression)(assignableStack.pop()));
        } else {
            assert !stack.isEmpty() || !assignableStack.isEmpty();
            Expression newLeft = !stack.isEmpty() ? stack.pop() : (Expression) assignableStack.pop();
            node.setLeft(newLeft);
        }

        // then we apply the exact same on the right expression of the binary
        // node
        node.getRight().accept(this);
        if (node.getRight() instanceof Identifier && LHS) {
            assert !assignableStack.isEmpty();
            node.setRight((Expression)(assignableStack.pop()));
        } else {
            assert !stack.isEmpty() || !assignableStack.isEmpty();
            Expression newRight = !stack.isEmpty() ? stack.pop() : (Expression) assignableStack.pop();
            node.setRight(newRight);
        }

        // now that node has been constant-folded we compute its value
        // and push the result onto the stack

        stack.push(BinarySymbol.compute(node));

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
            // if an if statement can be reduced to not having the guard
            // then we get rid of the guard
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
            } else if (b instanceof WhileStatement && //likewise for whiles
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
        // we make the boolean literal available by pushing it onto the stack
        stack.push(node);
    }

    /**
     * cast the character literal into its corresponding int value,
     * and add the int value to the list
     * @param node
     */
    public void visit(CharacterLiteral node) {
        // we convert the node into its int value
        Expression integerLiteral = new IntegerLiteral(""+(int)(node.getValue()));
        // and make the converted form readily available by pushing it onto
        // the stack
        stack.push(integerLiteral);
    }

    /**
     * we simplify the arguments to the function call and add them to a
     * newArguments list
     *
     * then we set newArguments to the arguments of node
     *
     * afterwards, we add the node to stack
     * @param node
     */
    public void visit(FunctionCall node) {
        // we perform constant folding on the arguments of the function call
        List<Expression> newArguments = new LinkedList<>();
        for(Expression e : node.getArguments()) {
            e.accept(this);
            assert !stack.isEmpty() || !assignableStack.isEmpty();
            Expression arg = !stack.isEmpty() ? stack.pop() :
                    (Expression) assignableStack.pop();
            newArguments.add(arg);
        }
        // then we set the arguments of the function call to the list of
        // constant folded arguments
        node.setArguments(new LinkedList<>(newArguments));

        // we make the function call available on the stack
        if (LHS) {
            assignableStack.push(node);
        } else {
            stack.push(node);
        }
    }

    /**
     * visit the methodblock
     * @param node
     */
    public void visit(FunctionDeclaration node) {
        BlockList methodBlock = node.getBlockList();
        methodBlock.accept(this);
    }

    /**
     * add the Identifier to stack
     * @param node
     */
    public void visit(Identifier node) {
        // if currently on LHS of assignment then we add it onto the
        // stack of assignables
        if (LHS) {
            assignableStack.push(node);
        } else { // otherwise we make the identifier available on the stack
            stack.push(node);
        }
    }

    /**
     * visit the guard, then set the guard to the visited guard
     * then accept the true block
     * if the false block is also present, visit it as well
     * @param node
     */
    public void visit(IfStatement node) {
        // we first perform constant folding on the guard of the
        // if statement
        node.getGuard().accept(this);
        // then we set its guard to the constant folded guard
        assert !stack.isEmpty();
        node.setGuard(stack.pop());

        // then we constant fold over the true block
        node.getTrueBlock().accept(this);

        // if a false block is present, we constant fold over that as well
        if (node.getFalseBlock().isPresent()) {
            node.getFalseBlock().get().accept(this);
        }
    }

    /**
     * add integer literal to stack
     * @param node
     */
    public void visit(IntegerLiteral node) {
        // we make the integer literal available on the stack
        stack.push(node);
    }

    /**
     * visit every argument to the procedure call
     * and add each visited argument to a newArguments list
     *
     * afterwards, set the arguments of procedure call
     * @param node
     */
    public void visit(ProcedureCall node) {
        // we perform constant folding on the arguments of a proc call
        List<Expression> newArguments = new LinkedList<>();
        for(Expression e : node.getArguments()) {
            e.accept(this);
            assert !stack.isEmpty();
            newArguments.add(stack.pop());
        }
        node.setArguments(new LinkedList<>(newArguments));
        // a proc call itself is a statement so we do not need to push it
        // onto the stack
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
        // we constant fold over values of the return statement
        List<Expression> returnValues = node.getValues();
        List<Expression> temp = new LinkedList<>();
        for (int i = 0; i < returnValues.size(); i++) {
            Expression expression = returnValues.get(i);
            expression.accept(this);
            assert !stack.isEmpty();
            temp.add(stack.pop());
        }
        node.setValues(temp);
    }

    /**
     * we convert the string literal to an int array
     * and add the array into stack
     * @param node
     */
    public void visit(StringLiteral node) {

        // we first get the char array representation of the string literal
        List<Character> charList = Util.backslashMergedCharList(node.getValue());

        // then we convert each char into an integer literal
        List<Expression> expressions = new LinkedList<>();
        for (int i = 0; i < charList.size(); i++) {
            expressions.add(new IntegerLiteral(""+(int)(charList.get(i))));
        }
        // we now have converted the string literal into an array literal
        Expression arr = new ArrayLiteral(expressions);

        // we now push this arr into the stack so it will be readily availble
        stack.push(arr);
    }

    /**
     * for a typed declaration, if the declaration involves a fixed size
     * arrays, then we visit each expression that specifies the size of
     * the array
     * @param node
     */
    public void visit(TypedDeclaration node) {
        // for a typed declaration, we can perform constant folding
        // on expressions that represent the size of a particular
        // array
        List<Expression> newArraySizeList = new LinkedList<>();
        for(Expression e : node.getArraySizeList()) {
            e.accept(this);
            assert !stack.isEmpty();
            newArraySizeList.add(stack.pop());
        }
        node.setArraySizeList(newArraySizeList);

        //TODO test
        // then we make the typed declaration available on the stack if it
        // is on the LHS of an assignment
        if (LHS) {
            assignableStack.push(node);
        }
    }


    /**
     * for a Unary node, we count how many times the unary operation
     * occurs and reduce it accordingly by the count and type of the expression
     * wrapped by the Unary
     * @param node
     */
    public void visit(Unary node) {

        Expression temp = node;
        // we start unlayering the unary node until the expression contained
        // by the Unary is no longer a Unary

        // because we start out with a unary node, we keep a variable
        // count that keeps track of how many times the unary operator
        // is layered. count is initialized at 1 since we start with a
        // node that is known as a Unary
        int count = 1;

        //we unlayer
        while(((Unary)temp).getExpression() instanceof Unary) {
            temp = ((Unary)temp).getExpression();
            count += 1;
        }
        // now that we unlayered to some type of value that was wrapped
        // in (possibly many) Unarys we "simplify" the value by applying
        // the visiting paradigm
        ((Unary)temp).getExpression().accept(this);

        //now the value we want is on the stack so we pop it and
        //use it as needed

        assert stack.size() > 0;
        Expression val = stack.pop();

        // if the number of the unary operator is an even number then
        // the unary operators can cancel out so we can just put the
        // value without any wrapping of the unary symbols on the stack
        if(count % 2 == 0) {
            // however, we have to be mindful of cases in which
            // the layering converts MIN_INT into a positive number
            // which would be out of bounds
            if(val instanceof IntegerLiteral &&
                    ((IntegerLiteral)val).getValue()
                            .compareTo("9223372036854775808") >= 0) {
                // in order to handle it, we use BigIntger to wrap
                // the expression and return its overflowed long value
                BigInteger bi = new BigInteger(((IntegerLiteral)val).getValue());
                stack.push(new IntegerLiteral(""+bi.longValue()));
            } else { // otherwise, we just push the pure form of the expression
                    // onto the stack
                stack.push(val);
            }
        } else { // otherwise, the number of layered unary operator is
                // odd so we push a Unary node with just ONE unary operator
               // of interest, as well as the value wrapped inside
            // if val is a boolean literal then we negate its value
            // and push it onto the stack
            if (val instanceof BooleanLiteral) {
                stack.push(new BooleanLiteral(!((BooleanLiteral)val).getValue()));
            } else if (val instanceof IntegerLiteral) {
                // if val is an integer literal then we use BigInteger to
                // multiply it by -1 and push onto the stack the negated value
                BigInteger longValue = new BigInteger(((IntegerLiteral)(val)).getValue());
                longValue = longValue.multiply(new BigInteger("-1"));
                stack.push(new IntegerLiteral(""+longValue.longValue()));
            } else {
                // otherwise we push a Unary object with just a single op
                // and the val of interest
                stack.push(new Unary(node.getOp(), val));
            }

        }
    }

    /**
     * add to stack of assignables
     * @param node
     */
    public void visit(Underscore node) {
      assignableStack.push(node);
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
        // we perform constant folding on the guard of the while loop
        node.getGuard().accept(this);
        assert !stack.isEmpty();
        node.setGuard(stack.pop());
        node.getBlock().accept(this);
    }
}
