package com.AST;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Type {

    public enum PrimitiveType {
        INT,
        BOOL
    }

    private PrimitiveType primitiveType;
    private ArrayBrackets arrayBrackets;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}


