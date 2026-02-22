package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

@FunctionalInterface
public interface ActionParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    Consumer<Player> parse(T element, ParserContext context);
}
