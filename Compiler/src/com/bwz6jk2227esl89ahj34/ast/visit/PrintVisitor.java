package com.bwz6jk2227esl89ahj34.ast.visit;

import com.bwz6jk2227esl89ahj34.ast.*;
import com.bwz6jk2227esl89ahj34.ast.type.ArrayType;
import com.bwz6jk2227esl89ahj34.ast.type.VariableType;
import com.bwz6jk2227esl89ahj34.util.prettyprint.CodeWriterSExpPrinter;

import java.lang.reflect.Method;
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
        for(Block b : node.getBlocks()){
            b.accept(this);
        }
    }

    public void visit(BooleanLiteral node) {
        printer.printAtom(node.getValue().toString());
    }

    public void visit(Break node) {
        // TODO
    }

    public void visit(CastedExpression node) {
        //TODO
    }

    public void visit(CharacterLiteral node) {
        printer.printAtom("'" + node.getValue() + "'");
    }

    // TODO: i keep getting mismatched blocks....
    public void visit(ClassDeclaration node) {
        printer.startList();
        printer.printAtom("class");
        node.getIdentifier().accept(this);
       // for (TypedDeclaration field : node.getFields()) {
       //     field.accept(this);
       // }
       // for (MethodDeclaration method : node.getMethods()) {
       //     method.accept(this);
       // }
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
        printer.startList();
        List<Identifier> argList = node.getArgumentIdentifiers();
        List<VariableType> argTypeList = node.getFunctionType().getArgTypes();
        List<VariableType> returnTypeList = node.getFunctionType()
                .getReturnTypeList().getVariableTypeList();
        for(int i = 0; i < node.getArgumentIdentifiers().size(); i++){
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

    public void visit(MethodDeclaration node) {
        node.getFunctionDeclaration().accept(this);
    }

    public void visit(Null node) {
        printer.printAtom("null");
    }

    public void visit(ObjectField node) {
        // TODO
    }

    public void visit(ObjectFunctionCall node) {
        // TODO
    }

    public void visit(ObjectInstantiation node) {
        // TODO
    }

    public void visit(ObjectProcedureCall node) {
        // TODO
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
        // adding this in to parse classes as well
        printer.startList();
        for (ClassDeclaration classDec : node.getClassDeclarations()) {
            classDec.accept(this);
        }
        printer.endList();
        // end parsing class declarations
        printer.startList();
        for (FunctionDeclaration funcDec : node.getFunctionDeclarations()) {
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

    public void visit(This node) {
        //TODO
    }

    public void printType(VariableType node) {
        if (!(node instanceof ArrayType)) {
            printer.printAtom(node.toString());
            return;
        }

        ArrayType arrayType = (ArrayType) node;
        for(int i = 0; i < arrayType.getNumBrackets(); i++) {
            printer.startList();
            printer.printAtom("[]");
        }
        printer.printAtom(arrayType.getBaseType().toString());
        for(int j = 0; j < arrayType.getNumBrackets(); j++) {
            printer.endList();
        }
    }

    public void visit(TypedDeclaration node) {
        printer.startList();
        node.getIdentifier().accept(this);

        VariableType type = node.getDeclarationType();

        if (!(type instanceof ArrayType)) {
            printer.printAtom(type.toString());
            return;
        }

        ArrayType arrayType = (ArrayType) type;
        for(int i = 0; i < arrayType.getNumBrackets(); i++) {
            printer.startList();
            printer.printAtom("[]");
        }

        printer.printAtom(arrayType.getBaseType().toString());

        int numArrayEmpty = arrayType.getNumBrackets() -
                node.getArraySizes().size();
        for(int j = 0; j < numArrayEmpty; j++) {
            printer.endList();
        }

        for(int k = node.getArraySizes().size()-1; k >= 0; k--) {
            node.getArraySizes().get(k).accept(this);
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
