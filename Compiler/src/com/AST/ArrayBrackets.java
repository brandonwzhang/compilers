package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Data
public class ArrayBrackets {
    private List<Optional<Expression>> indices;
}
