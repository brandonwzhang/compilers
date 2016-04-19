package com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.*;

public interface NodeVisitor {
    void visit(ArrayIndex node);
    void visit(ArrayLiteral node);
    void visit(Assignment node);
    void visit(Binary node);
    void visit(BlockList node);
    void visit(BooleanLiteral node);
    void visit(CharacterLiteral node);
    void visit(FunctionCall node);
    void visit(FunctionDeclaration node);
    void visit(Identifier node);
    void visit(IfStatement node);
    void visit(IntegerLiteral node);
    void visit(ProcedureCall node);
    void visit(Program node);
    void visit(ReturnStatement node);
    void visit(StringLiteral node);
    void visit(TypedDeclaration node);
    void visit(Unary node);
    void visit(Underscore node);
    void visit(UseStatement node);
    void visit(WhileStatement node);
}