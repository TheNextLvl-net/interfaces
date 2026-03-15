package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.InterfaceSession;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

/**
 * Provides a way to parse a JSON element into an action.
 *
 * @param <T> The type of JSON element to parse.
 * @since 0.2.0
 */
@FunctionalInterface
public interface ActionParser<T extends JsonElement> {
    /**
     * Parses a JSON element into an action.
     *
     * @param element The JSON element
     * @param context The parser context
     * @return The parsed action
     * @throws ParserException If the action could not be parsed
     * @since 0.2.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    Consumer<InterfaceSession> parse(T element, ParserContext context) throws ParserException;
}
