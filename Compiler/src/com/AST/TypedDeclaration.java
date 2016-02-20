package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@AllArgsConstructor
@Data
public class TypedDeclaration extends Statement implements Assignable {
    private Identifier id;
    private Type type;
}