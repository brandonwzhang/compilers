package com.AST;
import edu.cornell.cs.cs4120.util.*;

import java.util.Optional;

public class PrintVisitor implements NodeVisitor {

    private CodeWriterSExpPrinter printer;

    public PrintVisitor(CodeWriterSExpPrinter printer) {
        this.printer = printer;
    }

    public void visit(ArrayBrackets node) {
        /**taken care of in Type so never visited*/
    }

    public void visit(ArrayIndex node) {  //TODO: Ji Hun
        printer.startList();
        printer.printAtom("[]");
        node.getArrayRef().accept(this);
        node.getIndex().accept(this);
        printer.endList();
    }

    public void visit(ArrayLiteral node) {
        printer.startList();
        for(Expression e : node.getValues()){
            e.accept(this);
        }
        printer.endList();
    }

    public void visit(Assignment node) {
        printer.startList();
        printer.printAtom("=");
        for(Assignable a : node.getVariables()){
            a.accept(this);
        }
        node.getExpression().accept(this);
        printer.endList();
    }

    public void visit(Binary node) {
        printer.startList();
        printer.printAtom(node.getOp().toString());
        node.getLeft().accept(this);
        node.getRight().accept(this);
        printer.endList();
    }

    public void visit(BlockList node) {
        printer.startList();
        for(Block b : node.getBlockList()){
            b.accept(this);
        }
        printer.endList();
    }

    public void visit(BooleanLiteral node) {
        printer.printAtom(node.getValue().toString());
    }

    public void visit(CharacterLiteral node) {
        printer.printAtom("'" + node.getValue() + "'");
    }

    public void visit(FunctionBlock node) {
        printer.startList();
        node.getBlockList().accept(this);
        node.getReturnStatement().accept(this);
        printer.endList();
    }

    public void visit(FunctionCall node) {
        printer.startList();
        node.getIdentifier().accept(this);
        for(Expression e : node.getArguments()){
            e.accept(this);
        }
        printer.endList();
    }

    public void visit(FunctionDeclaration node) {
        printer.startList();
        node.getIdentifier().accept(this);
        for(TypedDeclaration td : node.getTypedDeclarationList()){
            td.accept(this);
        }
        for(Type t : node.getTypeList()){
            t.accept(this);
        }
        node.getFunctionBlock().accept(this);
        printer.endList();
    }

    public void visit(Identifier node) {
        printer.printAtom(node.getName());
    }

    public void visit(IfStatement node) {
        printer.startList();
        printer.printAtom("if");
        node.getGuard().accept(this);
        node.getTrueBlock().accept(this);
        if (node.getFalseBlock().isPresent()) {
            printer.printAtom("else");
            node.getFalseBlock().get().accept(this);
        }
        printer.endList();
    }

    public void visit(IntegerLiteral node) {
        printer.printAtom(node.getValue());
    }

    public void visit(Node node) {
        System.out.println("fuck you");
    }

    public void visit(ProcedureBlock node) {
        printer.startList();
        node.getBlockList().accept(this);
        printer.endList();
    }

    public void visit(ProcedureCall node) {
        printer.startList();
        node.getIdentifier().accept(this);
        for (Expression argument : node.getArguments()) {
            argument.accept(this);
        }
        printer.endList();
    }

    public void visit(Program node) {
        printer.startList();
        printer.startList();
        for (UseStatement useStatement : node.getUseBlock()) {
            useStatement.accept(this);
        }
        printer.endList();
        printer.startList();
        for (FunctionDeclaration funcDec : node.getFuncDecs()) {
            funcDec.accept(this);
        }
        printer.endList();
        printer.endList();
    }

    public void visit(ReturnStatement node) {
        printer.startList();
        printer.printAtom("return");
        for (Expression value : node.getValues()) {
            value.accept(this);
        }
        printer.endList();
    }

    public void visit(StringLiteral node) {
        printer.printAtom("\"" + node.getValue() + "\"");
    }

    /*
    public void visit(Type node) {
        for(int i = 0; i < node.getArrayBrackets().getIndices().size(); i++) {
            printer.startList();
            printer.printAtom("[]");
        }
        printer.printAtom(node.getPrimitiveType().toString());
        for (int j = node.getArrayBrackets().getIndices().size()-1;
             j >= 0; j--) {
            Optional<Expression> element =
                    node.getArrayBrackets().getIndices().get(j);
            if (element.isPresent()) {
                element.get().accept(this);
            }
            printer.endList();
        }
    }*/

    // TODO: make visit functions for the PrimitiveTypes

    public void visit(TypedDeclaration node) {
        printer.startList();
        node.getIdentifier().accept(this);
        node.getType().accept(this);
        printer.endList();
    }

    public void visit(Unary node) {
        printer.startList();
        printer.printAtom(node.getOp().toString());
        node.getExpression().accept(this);
        printer.endList();
    }

    public void visit(Underscore node) {
        printer.printAtom("_");
    }

    public void visit(UseStatement node) {
        printer.startList();
        printer.printAtom("use");
        node.getIdentifier().accept(this);
        printer.endList();
    }

    public void visit(WhileStatement node) {
        printer.startList();
        printer.printAtom("while");
        node.getGuard().accept(this);
        node.getBlock().accept(this);
        printer.endList();
    }
}
