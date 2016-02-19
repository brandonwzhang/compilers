package com.AST;

import java.util.List;
import java.util.Optional;

public class Assignment extends Statement {
    List<Optional<Assignable>> variables;
    Expression expression;
}
