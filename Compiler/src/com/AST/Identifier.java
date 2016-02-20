package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class Identifier extends Expression implements Assignable {
    private String name;
}
