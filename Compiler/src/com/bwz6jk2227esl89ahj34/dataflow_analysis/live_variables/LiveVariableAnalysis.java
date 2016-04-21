package com.bwz6jk2227esl89ahj34.dataflow_analysis.live_variables;

import com.bwz6jk2227esl89ahj34.assembly.*;
import com.bwz6jk2227esl89ahj34.assembly.AssemblyInstruction.OpCode;
import com.bwz6jk2227esl89ahj34.dataflow_analysis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiveVariableAnalysis extends DataflowAnalysis{

    public LiveVariableAnalysis(List<AssemblyLine> lines) {
        super(lines, Direction.BACKWARD);
    }

    /**
     * Returns the set of temps in this expression
     */
    private static Set<AssemblyAbstractRegister> tempsInExpression(AssemblyExpression expression) {
        Set<AssemblyAbstractRegister> temps = new HashSet<>();
        if (expression instanceof AssemblyAbstractRegister) {
            // We just need to add it to the set
            temps.add((AssemblyAbstractRegister) expression);
        } else if (expression instanceof AssemblyMemoryLocation) {
            // We need to check if either the base or offset registers are abstract
            AssemblyMemoryLocation location = (AssemblyMemoryLocation) expression;
            if (location.getBaseRegister() instanceof AssemblyAbstractRegister) {
                temps.add((AssemblyAbstractRegister)location.getBaseRegister());
            }
            if (location.getOffsetRegister() instanceof AssemblyAbstractRegister) {
                temps.add((AssemblyAbstractRegister)location.getOffsetRegister());
            }
        }
        return temps;
    }

    /**
     * Returns the set of temps used by this instruction
     */
    private static Set<AssemblyAbstractRegister> use(AssemblyInstruction instruction) {
        Set<AssemblyAbstractRegister> temps = new HashSet<>();
        if (instruction.getArgs().size() == 0) {
            // No arguments, so return the empty set
            return temps;
        }
        temps.addAll(tempsInExpression(instruction.getArgs().get(0)));
        if (instruction.getArgs().size() > 1) {
            // If we have more than one argument, it must be 2 arguments
            assert instruction.getArgs().size() == 2;
            if (instruction.opCode != OpCode.MOVQ) {
                // If it's a move, the dst expression is being defined, not used
                temps.addAll(tempsInExpression(instruction.getArgs().get(1)));
            }
        }
        return temps;
    }

    /**
     * Returns the set of temps defined by this instruction
     */
    private static Set<AssemblyAbstractRegister> def(AssemblyInstruction instruction) {
        Set<AssemblyAbstractRegister> temps = new HashSet<>();
        // We only have defs in the case of a move
        if (instruction.opCode != OpCode.MOVQ) {
            return temps;
        }
        assert instruction.getArgs().size() == 2;
        AssemblyExpression dst = instruction.getArgs().get(0);
        if (dst instanceof AssemblyAbstractRegister) {
            temps.add((AssemblyAbstractRegister) dst);
        }
        System.out.println(temps);
        return temps;
    }

    public void transfer(CFGNode node) {
        Set<LatticeElement> successorIns = new HashSet<>();
        for (CFGNode successor : node.getSuccessors()) {
            successorIns.add(successor.getIn());
        }
        // Set the out of this node to the meet of the in's of all predecessors
        node.setOut(meet(successorIns));
        AssemblyInstruction instruction = ((CFGNodeAssembly) node).getInstruction();
        // in[n] = use[n] + (out[n] - def[n])
        Set<AssemblyAbstractRegister> outSet =
                new HashSet<>(((LiveVariableSet) node.getOut()).getLiveVars());
        outSet.removeAll(def(instruction));
        Set<AssemblyAbstractRegister> inSet = use(instruction);
        inSet.addAll(outSet);

        node.setIn(new LiveVariableSet(inSet));
    }

    public LatticeElement meet(Set<LatticeElement> elements) {
        Set<AssemblyAbstractRegister> union = new HashSet<>();
        for (LatticeElement element : elements) {
            if (element instanceof LatticeTop) {
                continue;
            }
            if (element instanceof LatticeBottom) {
                return new LatticeBottom();
            }
            union.addAll(((LiveVariableSet)element).getLiveVars());
        }
        return new LiveVariableSet(union);
    }
}
