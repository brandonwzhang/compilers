package com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.*;
import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type.VariableType;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;

import java.util.List;

public class PrintVisitor implements NodeVisitor {

    private CodeWriterSExpPrinter printer;

    public PrintVisitor(CodeWriterSExpPrinter printer) {
        this.printer = printer;
    }

    public void visit(ArrayIndex node) {
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
        if(node.getVariables().size() > 1) {
            printer.startList();
        }
        for(Assignable a : node.getVariables()){
            a.accept(this);
        }
        if(node.getVariables().size() > 1) {
            printer.endList();
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
        for(Block b : node.getBlockList()){
            b.accept(this);
        }
    }

    public void visit(BooleanLiteral node) {
        printer.printAtom(node.getValue().toString());
    }

    public void visit(CharacterLiteral node) {
        printer.printAtom("'" + node.getValue() + "'");
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
        printer.startList();
        List<Identifier> argList = node.getArgList();
        List<VariableType> argTypeList = node.getFunctionType().getArgTypeList();
        List<VariableType> returnTypeList = node.getFunctionType()
                .getReturnTypeList().getVariableTypeList();
        for(int i = 0; i < node.getArgList().size(); i++){
            printer.startList();
            argList.get(i).accept(this);
            printType(argTypeList.get(i));
            printer.endList();
        }
        printer.endList();
        printer.startList();
        for(VariableType t : returnTypeList) {
            printType(t);
        }
        printer.endList();
        printer.startList();
        node.getBlockList().accept(this);
        printer.endList();
        printer.endList();
    }

    public void visit(Identifier node) {
        printer.printAtom(node.getName());
    }

    public static boolean isStatement(Block b) {
        return (b instanceof Statement);
    }

    public void visit(IfStatement node) {
        printer.startList();
        printer.printAtom("if");
        node.getGuard().accept(this);
        if(!isStatement(node.getTrueBlock())) {
            printer.startList();
        }
        node.getTrueBlock().accept(this);
        if(!isStatement(node.getTrueBlock())) {
            printer.endList();
        }
        if (node.getFalseBlock().isPresent()) {
            node.getFalseBlock().get().accept(this);
        }
        printer.endList();
    }

    public void visit(IntegerLiteral node) {
        printer.printAtom(node.getValue());
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
        for (FunctionDeclaration funcDec : node.getFunctionDeclarationList()) {
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


    public void printType(VariableType node) {
        for(int i = 0; i < node.getNumBrackets(); i++) {
            printer.startList();
            printer.printAtom("[]");
        }
        printer.printAtom(node.getPrimitiveType().toString());
        for(int j = 0; j < node.getNumBrackets(); j++) {
            printer.endList();
        }
    }

    public void visit(TypedDeclaration node) {
        printer.startList();
        node.getIdentifier().accept(this);
        for(int i = 0; i < node.getDeclarationType().getNumBrackets(); i++) {
            printer.startList();
            printer.printAtom("[]");
        }

        printer.printAtom(node.getDeclarationType().getPrimitiveType().toString());

        int numArrayEmpty = node.getDeclarationType().getNumBrackets() -
                node.getArraySizeList().size();
        for(int j = 0; j < numArrayEmpty; j++) {
            printer.endList();
        }

        for(int k = node.getArraySizeList().size()-1; k >= 0; k--) {
            node.getArraySizeList().get(k).accept(this);
            printer.endList();
        }

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
        if(!isStatement(node.getBlock())) {
            printer.startList();
        }
        node.getBlock().accept(this);
        if(!isStatement(node.getBlock())) {
            printer.endList();
        }
        printer.endList();
    }
}
