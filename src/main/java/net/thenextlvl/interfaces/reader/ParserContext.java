package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonObject;
import net.thenextlvl.interfaces.ClickAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ParserContext {
    // parses all (click) actions
    @Contract(pure = true)
    Optional<ClickAction> parseClickActions(JsonObject object);

    // parses all actions
    @Contract(pure = true)
    Optional<Consumer<Player>> parseActions(JsonObject object);

    // parses all conditions
    @Contract(pure = true)
    Optional<Predicate<Player>> parseConditions(JsonObject object);
}
