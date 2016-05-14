package com.bwz6jk2227esl89ahj34.ast.type;

import com.bwz6jk2227esl89ahj34.ast.Identifier;

public class Context {
    VariableContext variableContext;
    FunctionContext functionContext;

    public Context(Context c) {
        variableContext = new VariableContext();
        functionContext = new FunctionContext();
        for (Identifier key : c.getVariableContext().keySet()) {
            this.variableContext.put(key, c.getVariableContext().get(key));
        }
        for (Identifier key : c.getFunctionContext().keySet()) {
            this.functionContext.put(key, c.getFunctionContext().get(key));
        }
    }

    public Context(VariableContext variableContext, FunctionContext functionContext) {
        this.variableContext = variableContext;
        this.functionContext = functionContext;
    }

    public VariableContext getVariableContext() {
        return this.variableContext;
    }

    public FunctionContext getFunctionContext() {
        return this.functionContext;
    }

    public void put(Identifier identifier, Type type) {
        if (type instanceof VariableType) {
            variableContext.put(identifier, type);
        } else {
            functionContext.put(identifier, type);
        }

    }
}
