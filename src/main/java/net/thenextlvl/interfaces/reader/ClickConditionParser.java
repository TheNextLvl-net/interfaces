package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.ClickContext;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;

/**
 * Provides a way to parse a JSON element into a click condition.
 *
 * @param <T> The type of JSON element to parse.
 * @since 0.4.0
 */
@FunctionalInterface
public interface ClickConditionParser<T extends JsonElement> {
    /**
     * Parses a JSON element into a click condition.
     *
     * @param element The JSON element
     * @param context The parser context
     * @return The parsed click condition
     * @throws ParserException If the click condition could not be parsed
     * @since 0.4.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    Predicate<ClickContext> parse(T element, ParserContext context) throws ParserException;
}
