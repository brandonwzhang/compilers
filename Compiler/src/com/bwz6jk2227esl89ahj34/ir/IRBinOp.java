package com.bwz6jk2227esl89ahj34.ir;

import com.bwz6jk2227esl89ahj34.ir.visit.CheckConstFoldedIRVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.MIRVisitor;
import com.bwz6jk2227esl89ahj34.util.InternalCompilerError;
import com.bwz6jk2227esl89ahj34.util.prettyprint.SExpPrinter;
import com.bwz6jk2227esl89ahj34.ir.visit.AggregateVisitor;
import com.bwz6jk2227esl89ahj34.ir.visit.IRVisitor;

import java.util.LinkedList;
import java.util.List;

/**
 * An intermediate representation for a binary operation
 * OP(left, right)
 */
public class IRBinOp extends IRExpr {

    /**
     * Binary operators
     */
    public enum OpType {
        ADD, SUB, MUL, HMUL, DIV, MOD, AND, OR, XOR, LSHIFT, RSHIFT, ARSHIFT,
        EQ, NEQ, LT, GT, LEQ, GEQ;

        @Override
        public String toString() {
            switch (this) {
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case HMUL:
                return "HMUL";
            case DIV:
                return "DIV";
            case MOD:
                return "MOD";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case XOR:
                return "XOR";
            case LSHIFT:
                return "LSHIFT";
            case RSHIFT:
                return "RSHIFT";
            case ARSHIFT:
                return "ARSHIFT";
            case EQ:
                return "EQ";
            case NEQ:
                return "NEQ";
            case LT:
                return "LT";
            case GT:
                return "GT";
            case LEQ:
                return "LEQ";
            case GEQ:
                return "GEQ";
            }
            throw new InternalCompilerError("Unknown op type");
        }
    };

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    /**
     * Copy constructor (shallow).
     * @param binop
     */
    public IRBinOp(IRBinOp binop) {
        this.type = binop.type;
        this.left = binop.left;
        this.right = binop.right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr left = (IRExpr) v.visit(this, this.left);
        IRExpr right = (IRExpr) v.visit(this, this.right);

        if (left != this.left || right != this.right)
            return new IRBinOp(type, left, right);

        return this;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        return !isConstant();
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(left));
        result = v.bind(result, v.visit(right));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }

    @Override
    public IRNode leave(IRVisitor v, IRNode n, IRNode n_) {
        assert n_ instanceof IRBinOp;
        IRBinOp irb = (IRBinOp)(n_);
        assert irb.left() instanceof IRESeq;
        assert irb.right() instanceof IRESeq;
        List<IRStmt> lst = new LinkedList<>();
        addStatements(lst, ((IRESeq)(irb.left())).stmt());
        String t = MIRVisitor.getFreshVariable();
        addStatements(lst,
                new IRMove(new IRTemp(t), ((IRESeq)(irb.left())).expr()));
        addStatements(lst, ((IRESeq)(irb.right())).stmt());
        return new IRESeq(new IRSeq(lst),
                new IRBinOp(irb.opType(), new IRTemp(t),
                        ((IRESeq)(irb.right())).expr()));
    }

}
