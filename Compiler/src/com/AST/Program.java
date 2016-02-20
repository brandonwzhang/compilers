package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Program {
    private List<UseStatement> useBlock;
    private List<FunctionDeclaration> funcDecs;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
