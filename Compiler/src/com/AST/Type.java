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
        INT, BOOL;

        @Override public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private PrimitiveType primitiveType;
    private Integer numBrackets;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}


