package edu.cornell.cs.cs4120.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import polyglot.util.CodeWriter;
import polyglot.util.OptimalCodeWriter;

/**
 * An {@linkplain SExpPrinter} implementation designed to print ASTs through a
 * provided {@link CodeWriter}.
 */
public class CodeWriterSExpPrinter implements SExpPrinter {
    private final CodeWriter writer;

    private boolean addSpace;

    /**
     * Constructs a new {@linkplain SExpPrinter} instance that prints programs
     * using the given {@code CodeWriter}.
     *
     * @param writer
     *          the {@code CodeWriter} to print to
     */
    public CodeWriterSExpPrinter(CodeWriter writer) {
        this.writer = writer;
    }

    /**
     * Constructs a new {@linkplain SExpPrinter} instance that prints programs
     * using the given writer.  Output is kept to 80 columns if possible.
     *
     * @param w
     *          the writer to write to
     */
    public CodeWriterSExpPrinter(PrintWriter w) {
        this(new OptimalCodeWriter(w, 80));
    }

    /**
     * Constructs a new {@linkplain SExpPrinter} instance that prints programs
     * using the given stream.  Output is kept to 80 columns if possible.
     * Deprecated: use the previous constructor instead.
     *
     * @param o
     *          the output stream to print to
     */
    @Deprecated
    public CodeWriterSExpPrinter(OutputStream o) {
        this(new OptimalCodeWriter(o, 80));
    }

    @Override
    public void printAtom(String atom) {
        if (addSpace)
            writer.allowBreak(0);
        else addSpace = true;
        writer.write(atom);
    }

    @Override
    public void startList() {
        if (addSpace) writer.allowBreak(0);
        writer.write("(");
        writer.allowBreak(2, 2, "", 0); // miser mode
        writer.begin(0);
        addSpace = false;
    }

    @Override
    public void endList() {
        writer.end();
        writer.write(")");
        addSpace = true;
    }

    @Override
    public void flush() {
        try {
            writer.newline();
            writer.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            flush();
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
