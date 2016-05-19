package com.bwz6jk2227esl89ahj34.ast.visit;

import com.bwz6jk2227esl89ahj34.ast.*;

public interface NodeVisitor {
    void visit(ArrayIndex node);
    void visit(ArrayLiteral node);
    void visit(Assignment node);
    void visit(Binary node);
    void visit(BlockList node);
    void visit(BooleanLiteral node);
    void visit(Break node);
    void visit(CastedExpression node);
    void visit(CharacterLiteral node);
    void visit(ClassDeclaration node);
    void visit(FunctionCall node);
    void visit(FunctionDeclaration node);
    void visit(Identifier node);
    void visit(IfStatement node);
    void visit(InstanceOf node);
    void visit(IntegerLiteral node);
    void visit(MethodDeclaration node);
    void visit(Null node);
    void visit(ObjectField node);
    void visit(ObjectFunctionCall node);
    void visit(ObjectInstantiation node);
    void visit(ObjectProcedureCall node);
    void visit(ProcedureCall node);
    void visit(Program node);
    void visit(ReturnStatement node);
    void visit(StringLiteral node);
    void visit(This node);
    void visit(TypedDeclaration node);
    void visit(Unary node);
    void visit(Underscore node);
    void visit(UseStatement node);
    void visit(WhileStatement node);
}
