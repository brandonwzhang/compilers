package com.AST;

import java.util.List;

public class Assignment extends Statement {
    private List<Assignable> variables;
    private Expression expression;
}
