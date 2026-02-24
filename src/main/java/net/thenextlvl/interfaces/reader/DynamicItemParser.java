package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.RenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface DynamicItemParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    BiConsumer<ItemStack, RenderContext> parse(T element, ParserContext context) throws ParserException;
}
