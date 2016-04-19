package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionCall extends Expression implements Assignable {
    private Identifier identifier;
    private List<Expression> arguments;

    public void accept(NodeVisitor v){
        v.visit(this);
    }
}
