package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.RenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.BiFunction;

/**
 * Provides a way to parse a JSON element into an item within a render context.
 *
 * @param <T> The type of JSON element to parse.
 * @since 0.2.0
 */
@FunctionalInterface
public interface DynamicItemParser<T extends JsonElement> {
    /**
     * Parses a JSON element into an item within a render context.
     *
     * @param element The JSON element
     * @param context The parser context
     * @return The parsed item within the render context
     * @throws ParserException If the item could not be parsed
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    BiFunction<ItemStack, RenderContext, ItemStack> parse(T element, ParserContext context) throws ParserException;
}
