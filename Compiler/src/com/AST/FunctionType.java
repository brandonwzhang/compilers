package com.AST;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionType extends Type {
    List<VariableType> argumentTypes;
    List<VariableType> returnValueTypes;

}
