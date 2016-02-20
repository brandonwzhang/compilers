package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class ReturnStatement extends Node {
    private List<Expression> values;
}
