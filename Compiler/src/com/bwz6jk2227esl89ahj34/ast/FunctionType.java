package com.bwz6jk2227esl89ahj34.ast;

import com.bwz6jk2227esl89ahj34.ast.type.Type;
import com.bwz6jk2227esl89ahj34.ast.type.VariableType;
import com.bwz6jk2227esl89ahj34.ast.type.VariableTypeList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionType extends Type {
    List<VariableType> argTypeList;
    VariableTypeList returnTypeList;
}
