package com.AST;

import lombok.Data;

public @Data class Identifier extends Node implements Assignable {
    private String name;
}
