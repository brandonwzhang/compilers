package com.bwz6jk2227esl89ahj34.abstract_syntax_tree;

import com.bwz6jk2227esl89ahj34.abstract_syntax_tree.visit.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FunctionDeclaration extends Node {
    private Identifier identifier;
    private FunctionType functionType;
    private List<Identifier> argList;
    private BlockList blockList;

    public void accept(NodeVisitor v){
        v.visit(this);
    }

}