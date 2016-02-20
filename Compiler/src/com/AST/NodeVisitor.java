package com.AST;

public interface NodeVisitor {


    public void visit(ArrayBrackets node);
    public void visit(ArrayIndex node);
    public void visit(ArrayLiteral node);
    public void visit(Assignment node);
    public void visit(Binary node);
    public void visit(BlockList node);
    public void visit(BooleanLiteral node);
    public void visit(CharacterLiteral node);
    public void visit(FunctionBlock node);
    public void visit(FunctionCall node);
    public void visit(FunctionDeclaration node);
    public void visit(Identifier node);
    public void visit(IfStatement node);
    public void visit(IntegerLiteral node);
    public void visit(ProcedureCall node);
    public void visit(Program node);
    public void visit(ReturnStatement node);
    public void visit(Statement node);
    public void visit(StringLiteral node);
    public void visit(Type node);
    public void visit(TypedDeclaration node);
    public void visit(Unary node);
    public void visit(Underscore node);
    public void visit(UseStatement node);
    public void visit(WhileStatement node);

}
