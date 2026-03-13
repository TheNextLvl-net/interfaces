package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;

@FunctionalInterface
public interface ItemParser<T extends JsonElement> {
    /**
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    Function<ItemStack, ItemStack> parse(T element, ParserContext context) throws ParserException;
}
