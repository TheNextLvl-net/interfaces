package net.thenextlvl.interfaces.reader;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public final class ParserConditions {
    private ParserConditions() {
    }

    @Contract(value = "false, _, _ -> fail", pure = true)
    public static void checkState(final boolean expression, @PrintFormat final String message, final Object... args) throws ParserException {
        if (!expression) throw new ParserException(message, args);
    }

    @CanIgnoreReturnValue
    @Contract(value = "null, _, _ -> fail; !null, _, _ -> !null", pure = true)
    public static <T> T checkNonNull(final @Nullable T reference, @PrintFormat final String message, final Object... args) throws ParserException {
        if (reference == null) throw new ParserException(message, args);
        return reference;
    }
}
