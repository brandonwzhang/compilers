package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Context {
    VariableContext variableContext;
    FunctionContext functionContext;

    public Context(Context c) {
        this(c.getVariableContext(), c.getFunctionContext());
    }

    public void put(Identifier identifier, Type type) {
        if (type instanceof VariableType) {
            variableContext.put(identifier, type);
        } else {
            functionContext.put(identifier, type);
        }

    }
}
