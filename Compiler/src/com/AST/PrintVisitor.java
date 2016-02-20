package com.AST;


public class PrintVisitor implements NodeVisitor {

    private CodeWriterSExpPrinter printer;

    public PrintVisitor(CodeWriterSExpPrinter printer) {
        this.printer = printer;
    }

    public void visit(ArrayBrackets node) {
        printer.startList();
        // TODO: Ji Hun God
        printer.endList();
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
        printer.printAtom(node.getValue());
    }

    public void visit(CharacterLiteral node) {
        printer.printAtom(node.getValue());
    }

    public void visit(FunctionBlock node) {
        printer.startLine();
        node.getBlockList().accept(this);
        node.getReturnStatement().accept(this);
        printer.endLine();
    }

    public void visit(FunctionCall node) {
        printer.startLine();
        node.getIdentifier().accept(this);
        for(Expression e : node.getArguments()){
            e.accept(this);
        }
        printer.endLine();
    }

    public void visit(FunctionDeclaration node) {
        printer.startLine();
        node.getIdentifier().accept(this);
        for(TypeDeclaration td : node.getTypedDeclarationList()){
            td.accept(this);
        }
        for(Type t : node.getTypeList()){
            t.accept(this);
        }
        node.getFunctionBlock().accept(this);
        printer.endLine();
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
            node.getFalseBlock().get().accept(this);
        }
        printer.endList();
    }

    public void visit(IntegerLiteral node) {
        printer.printAtom(node.getValue());
    }

    public void visit(Node node) throws Exception {
        throw new Exception("visit should not be called on a node with " +
                "compile-time type Node");
    }

    public void visit(ProcedureCall node) {
        printer.startList();
        node.getIdentifier().accept(this);
        for (argument : node.getArguments()) {
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
    }

    public void visit(ReturnStatement node) {
        printer.startList();
        printer.printAtom("return");
        for (value : node.getValues()) {
            value.accept(this);
        }
        printer.endList();
    }

    public void visit(StringLiteral node) {
        printer.printAtom(node.getValue());
    }

    public void visit(Type node) {


        int[0][1] -> ([] ([] int 1) 0)

    } //TODO: Ji Hun

    public void visit(TypedDeclaration node) {
        printer.startLine();
        node.getIdentifier().accept(this);
        node.getType().accept(this);
        printer.endLine();
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
