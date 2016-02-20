package com.AST;

import lombok.Data;

import java.util.List;
import java.util.Optional;

public @Data class ArrayBrackets {
    private List<Optional<Expression>> indices;
}
