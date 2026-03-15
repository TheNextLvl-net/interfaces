package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.ClickAction;
import org.jetbrains.annotations.Contract;

/**
 * Provides a way to parse a JSON element into a {@link ClickAction}.
 *
 * @param <T> The type of JSON element to parse.
 * @since 0.2.0
 */
@FunctionalInterface
public interface ClickActionParser<T extends JsonElement> {
    /**
     * Parses a JSON element into a {@link ClickAction}.
     *
     * @param element The JSON element to parse.
     * @param context The parser context.
     * @return The parsed {@link ClickAction}.
     * @throws ParserException If an error occurs during parsing.
     * @since 0.2.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    ClickAction parse(T element, ParserContext context) throws ParserException;
}
