package com.AST;

import lombok.Data;
import java.util.List;

public @Data class ReturnStatement extends Node {
    List<Expression> values;
}
