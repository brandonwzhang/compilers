package com.bwz6jk2227esl89ahj34.ir.visit;

import com.bwz6jk2227esl89ahj34.dataflow_analysis.available_copies
        .AvailableCopiesSet;
import com.bwz6jk2227esl89ahj34.ir.IRMem;
import com.bwz6jk2227esl89ahj34.ir.IRMove;
import com.bwz6jk2227esl89ahj34.ir.IRNode;
import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.ir.interpret.Configuration;

import java.util.HashSet;
import java.util.Set;

public class AvailableCopiesVisitor extends IRVisitor {
    private AvailableCopiesSet set;

    public AvailableCopiesVisitor(AvailableCopiesSet set) {
        this.set = set;
    }

    /**
     * If n_ is an IRTemp, we return n_ with the name replaced with its mapping
     * in map. If there exists no mapping or n_ is not an IRTemp, we just
     * return n_.
     * It also serves a second purpose of adding all encountered temp names to
     * a set for use in available copies analysis.
     */
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        if (n_ instanceof IRMove) {
            IRMove move = (IRMove) n;
            if (move.target() instanceof IRMem) {
                return n_;
            }
            if (move.target() instanceof IRTemp) {
                IRTemp temp = (IRTemp) move.target();
                // We don't rename the temp if it's a return temp since the caller
                // relies on them
                if (temp.name().length() > 4 &&
                        (temp.name().substring(0, 4).equals(Configuration.ABSTRACT_RET_PREFIX) ||
                        temp.name().substring(0, 4).equals(Configuration.ABSTRACT_ARG_PREFIX))) {
                    return n_;
                }
            }
            if (move.expr() instanceof IRTemp) {
                IRTemp temp = (IRTemp) move.expr();
                // We don't rename the temp if it's a return temp since the caller
                // relies on them
                if (temp.name().length() > 4 &&
                        (temp.name().substring(0, 4).equals(Configuration.ABSTRACT_RET_PREFIX) ||
                        temp.name().substring(0, 4).equals(Configuration.ABSTRACT_ARG_PREFIX))) {
                    return n_;
                }
                IRTemp newtemp = new IRTemp(getMapping(temp.name()));
                return new IRMove(move.target(), newtemp);
            }
        }
        return n_;
    }

    private String getMapping(String name) {
        String mapping = set.get(name);
        if (mapping == null) {
            return name;
        }
        return getMapping(mapping);
    }
}
