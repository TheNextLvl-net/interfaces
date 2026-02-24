package net.thenextlvl.interfaces.reader;

import org.intellij.lang.annotations.PrintFormat;

public class ParserException extends Exception {
    public ParserException(@PrintFormat final String message, final Object... args) {
        super(String.format(message, args));
    }

    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
