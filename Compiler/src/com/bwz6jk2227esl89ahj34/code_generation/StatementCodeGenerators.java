package com.bwz6jk2227esl89ahj34.code_generation;

import com.bwz6jk2227esl89ahj34.AST.WhileStatement;
import com.bwz6jk2227esl89ahj34.ir.*;
import com.bwz6jk2227esl89ahj34.code_generation.AssemblyPhysicalRegister.Register;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.bwz6jk2227esl89ahj34.code_generation.AssemblyInstruction.*;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;
import com.sun.deploy.config.Config;

public class StatementCodeGenerators {

    public StatementCodeGenerators() {

    }

    public static StatementTile.CodeGenerator move1 = (root) -> {
        /*
            MOVE(dst, src)
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRMove castedRoot = (IRMove) root;
        AssemblyExpression src = TileContainer.matchExpression(castedRoot.expr(), instructions);
        AssemblyExpression dst = TileContainer.matchExpression(castedRoot.target(), instructions);

        assert !(dst instanceof AssemblyImmediate);
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, src, dst));

        return instructions;
    };

    public static StatementTile.CodeGenerator jump1 = (root) -> {
        /*
            JUMP(label)
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRJump castedRoot = (IRJump) root;
        AssemblyExpression label = TileContainer.matchExpression(castedRoot.target(), instructions);

        assert label instanceof AssemblyName;
        instructions.add(new AssemblyInstruction(OpCode.JMP, label));

        return instructions;
    };

    public static StatementTile.CodeGenerator label1 = (root) -> {
        /*
            LABEL(name)
         */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRLabel castedRoot = (IRLabel) root;
        AssemblyLabel label = new AssemblyLabel(new AssemblyName(castedRoot.name()));

        instructions.add(label);
        return instructions;
    };

    public static StatementTile.CodeGenerator exp1 = (root) -> {
      	/*
        		EXP(CALL(NAME))
        */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRExp castedRoot = (IRExp) root;
        // TODO
        return instructions;
    };

    public static StatementTile.CodeGenerator move2 = (root) -> {
    		/*
        		MOVE(TEMP, CALL(NAME))
        */
        return null;
    };

    /**
     * For convenience, we take care of the "function epilogue"
     * along with the return statement 
     */
    public static StatementTile.CodeGenerator return1 = (root) -> {
        /*
        		RETURN()
        */
        List<AssemblyInstruction> instructions = new LinkedList<>();
        // Restore callee-save registers
        for (int i = 0; i < AssemblyPhysicalRegister.calleeSavedRegisters.length; i++) {
            AssemblyPhysicalRegister register = AssemblyPhysicalRegister.calleeSavedRegisters[i];
            instructions.add(new AssemblyInstruction(
                    OpCode.MOVQ,
                    register,
                    AssemblyMemoryLocation.stackOffset(Configuration.WORD_SIZE * (1 + i))
                    ));
        }
        // Restore old RBP and RSP
        instructions.add(new AssemblyInstruction(OpCode.MOVQ, AssemblyPhysicalRegister.RBP, AssemblyPhysicalRegister.RSP));
        instructions.add(new AssemblyInstruction(OpCode.POPQ, AssemblyPhysicalRegister.RBP));
        instructions.add(new AssemblyInstruction(OpCode.RETQ));
        return instructions;
    };

    public static StatementTile.CodeGenerator cjump1 = (root) -> {
        /*
            CJUMP(e, trueLabel)
         */
        LinkedList<AssemblyInstruction> instructions = new LinkedList<>();
        IRCJump castedRoot = (IRCJump) root;
        assert castedRoot.falseLabel() == null; // assert lowered CJump
        AssemblyExpression guard = TileContainer.matchExpression(castedRoot.expr(), instructions);

        // compare guard to 0, jump to trueLabel if not equal
        instructions.add(new AssemblyInstruction(OpCode.CMP, guard, new AssemblyImmediate(0)));
        instructions.add(new AssemblyInstruction(OpCode.JNE, new AssemblyName(castedRoot.trueLabel())));

        return instructions;
    };

    private static void functionCall(IRNode node, List<AssemblyInstruction> instructions) {

        assert node instanceof IRCall;
        IRCall castedNode = (IRCall) node;

        // Save all caller-saved registers
        AssemblyPhysicalRegister.saveToStack(instructions, AbstractAssemblyGenerator.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

        // pass pointer to return argument space as first argument (RDI)
        instructions.add(new AssemblyInstruction(
                OpCode.MOVQ,
                AssemblyMemoryLocation.stackOffset(AbstractAssemblyGenerator.getReturnValuesOffset()),
                AssemblyPhysicalRegister.RDI
        ));

        // put arguments in order rsi rdx rcx r8 r9
        List<IRExpr> arguments = castedNode.args();
        List<Register> argumentRegisters = Arrays.asList(
                Register.RSI,
                Register.RDX,
                Register.RCX,
                Register.R8,
                Register.R9
        );
        int numArguments = arguments.size();
        Stack<AssemblyExpression> reversedArguments = new Stack<>();
        for(int i = 0; i < numArguments; i++) {
            if (i < 5) {
                AssemblyExpression e = TileContainer.matchExpression(arguments.get(i), instructions);
                instructions.add(
                        new AssemblyInstruction(
                                OpCode.MOVQ,
                                TileContainer.matchExpression(arguments.get(i), instructions),
                                e,
                                new AssemblyPhysicalRegister(argumentRegisters.get(i))
                        )
                );
            } else { // push onto stack
                reversedArguments.push(
                        TileContainer.matchExpression(arguments.get(i), instructions)
                );
            }
        }
        // further arguments go in stack in reverse order
        AssemblyPhysicalRegister.saveToStack(instructions, AbstractAssemblyGenerator.getArgumentsOffset(),
                reversedArguments.toArray(new AssemblyPhysicalRegister[reversedArguments.size()]));

         // get function name
        AssemblyExpression name = TileContainer.matchExpression(castedNode.target(), instructions);
        assert name instanceof AssemblyName;

        // add the call instruction to instructions
        instructions.add(
                new AssemblyInstruction(
                        OpCode.CALL,
                        name
                )
        );

        functionCallEpilogue(castedNode.args(), instructions);

    }

    private static void functionCallEpilogue(List<IRExpr> args, List<AssemblyInstruction> instructions) {
        int numArguments = args.size();

        // if number of args was greater than 5, then we computed
        // rdi as well as the first 5 into rdi...r9
        // and the rest of the args are in the stack
        // ... to ignore these args, we increment rsp
        if (numArguments > 5) {
            instructions.add(
                    new AssemblyInstruction(
                            OpCode.ADDQ,
                            new AssemblyImmediate(8*(numArguments - 5)),
                            new AssemblyPhysicalRegister(Register.RSP)
                    )
            );
        }

        // now we restore all of the caller saved registers
        AssemblyPhysicalRegister.restoreFromStack(instructions, AbstractAssemblyGenerator.getCallerSpaceOffset(),
                AssemblyPhysicalRegister.callerSavedRegisters);

        // TODO: undo space to the callee function
        // TODO: undo space for return values
    }
}
