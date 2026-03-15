package net.thenextlvl.interfaces.reader;

import org.intellij.lang.annotations.PrintFormat;

/**
 * An exception that is thrown when a parser encounters an error.
 *
 * @since 0.2.0
 */
public class ParserException extends Exception {
    /**
     * Creates a new parser exception.
     *
     * @param message The error message
     * @param args    The arguments for the error message
     * @since 0.2.0
     */
    public ParserException(@PrintFormat final String message, final Object... args) {
        super(String.format(message, args));
    }

    /**
     * Creates a new parser exception.
     *
     * @param message The error message
     * @param cause   The cause of the exception
     * @param args    The arguments for the error message
     * @since 0.4.0
     */
    public ParserException(@PrintFormat final String message, final Throwable cause, final Object... args) {
        super(String.format(message, args), cause);
    }
}
