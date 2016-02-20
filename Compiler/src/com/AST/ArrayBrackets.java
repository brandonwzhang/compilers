package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ArrayBrackets {
    private List<Optional<Expression>> indices;
}
