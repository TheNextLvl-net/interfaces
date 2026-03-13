package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.RenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.BiFunction;

@FunctionalInterface
public interface DynamicItemParser<T extends JsonElement> {
    /**
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    BiFunction<ItemStack, RenderContext, ItemStack> parse(T element, ParserContext context) throws ParserException;
}
