package com.AST;

import lombok.Data;

import java.util.List;

public @Data class Statement extends Block {
    public static @Data class ProcedureCall extends Statement {
        private Identifier identifier;
        private List<Expression> arguments;
    }
    public static @Data class IfStatement extends Statement {
        private Expression guard;
        private Block trueBlock;
        private Block falseBlock;
    }
    public static @Data class WhileStatement extends Statement {
        private Expression guard;
        private Block block;
    }
    public static @Data class TypedDeclarationList extends Statement {
        private List<TypedDeclaration> typedDeclarationList;
    }
    public static @Data class Assignment extends Statement {
        private TypedDeclaration typedDeclaration;
        private Identifier identifier;
        private Expression expression;
    }
}
