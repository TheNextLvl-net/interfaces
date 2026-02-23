package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

@FunctionalInterface
public interface ItemParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    Consumer<ItemStack> parse(T element, ParserContext context);
}
