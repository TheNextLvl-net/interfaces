package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;

@FunctionalInterface
public interface ConditionParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    Predicate<Player> parse(T element, ParserContext context);
}
