package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class Type extends Node {

    public enum PrimitiveType {
        INT,
        BOOL
    }

    private PrimitiveType basicType;

    private ArrayBrackets arrayBrackets;
}
