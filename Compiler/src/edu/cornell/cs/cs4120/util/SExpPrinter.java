package edu.cornell.cs.cs4120.util;

/**
 * A pretty-printer for S-expressions.
 */
public interface SExpPrinter extends AutoCloseable {
    /**
     * Prints an atom.
     *
     * @param atom
     *          the atom to print
     */
    void printAtom(String atom);

    /**
     * Prints the open parenthesis of an S-expression list.
     */
    void startList();

    /**
     * Prints the close parenthesis of an S-expression list.
     */
    void endList();

    /**
     * Flushes all formatted text to the underlying writer.
     * This method should be called when printing is complete.
     */
    void flush();

    /**
     * Flushes and closes both this printer and any underlying
     * output stream to which it is attached. It is an error to
     * perform any subsequent operations on the same printer.
     */
    @Override
    void close();
}
