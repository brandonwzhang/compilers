package com.AST;


public class PrintVisitor implements NodeVisitor {
    void visit(ArrayBrackets node);
    void visit(ArrayIndex node);
    void visit(ArrayLiteral node);
    void visit(Assignment node);
    void visit(Binary node);
    void visit(BlockList node);
    void visit(BooleanLiteral node);
    void visit(CharacterLiteral node);
    void visit(FunctionBlock node);
    void visit(FunctionCall node);
    void visit(FunctionDeclaration node);
    void visit(Identifier node);
    void visit(IfStatement node);
    void visit(IntegerLiteral node);
    void visit(Node node);
    void visit(ProcedureCall node);
    void visit(Program node);
    void visit(ReturnStatement node);
    void visit(StringLiteral node);
    void visit(Type node);
    void visit(TypedDeclaration node);
    void visit(Unary node);
    void visit(Underscore node);
    void visit(UseStatement node);
    void visit(WhileStatement node);
}
