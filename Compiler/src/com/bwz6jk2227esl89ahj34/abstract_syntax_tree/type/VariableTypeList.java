package com.bwz6jk2227esl89ahj34.abstract_syntax_tree.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class VariableTypeList extends Type {
    List<VariableType> variableTypeList;

}
