package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Assignment extends Statement {
    private List<Assignable> variables;
    private Expression expression;
}
