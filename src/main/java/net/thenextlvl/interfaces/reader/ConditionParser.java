package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.InterfaceSession;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;

/**
 * Provides a way to parse a JSON element into a condition.
 *
 * @param <T> The type of JSON element to parse.
 * @since 0.2.0
 */
@FunctionalInterface
public interface ConditionParser<T extends JsonElement> {
    /**
     * Parses a JSON element into a condition.
     *
     * @param element The JSON element
     * @param context The parser context
     * @return The parsed condition
     * @throws ParserException If the condition could not be parsed
     * @since 0.2.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    Predicate<InterfaceSession> parse(T element, ParserContext context) throws ParserException;
}
