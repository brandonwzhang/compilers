package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TypedDeclaration extends Statement implements Assignable {
    private Identifier id;
    private Type type;
}