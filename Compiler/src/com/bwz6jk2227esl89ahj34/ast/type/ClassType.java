package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassType extends BaseType {
    private Identifier identifier;
    private Optional<Identifier> parentIdentifier;
    private Map<Identifier, Type> fields;
    private Map<Identifier, FunctionType> methods;

    public static ClassType construct(ClassDeclaration cd) {
        Identifier identifier = cd.getIdentifier();
        Optional<Identifier> parentIdentifier = cd.getParentIdentifier();
        Map<Identifier, Type> fields = new HashMap<>();
        for (TypedDeclaration field : cd.getFields()) {
            fields.put(field.getIdentifier(), field.getDeclarationType());
        }
        Map<Identifier, FunctionType> methods = new HashMap<>();
        for (MethodDeclaration method : cd.getMethods()) {
            FunctionDeclaration fd = method.getFunctionDeclaration();
            methods.put(fd.getIdentifier(), fd.getFunctionType());
        }
        return new ClassType(identifier, parentIdentifier, fields, methods);
    }
}
