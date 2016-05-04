package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassType extends BaseType {
    private Identifier identifier;
    private Identifier parentIdentifier;
    private Map<Identifier, Type> fields;
    private Map<Identifier, FunctionType> methods;
}
