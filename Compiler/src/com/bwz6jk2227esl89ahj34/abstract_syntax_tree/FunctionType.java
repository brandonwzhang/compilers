package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type.Type;
import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type.VariableType;
import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type.VariableTypeList;
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
