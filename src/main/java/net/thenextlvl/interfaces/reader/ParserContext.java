package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.InterfaceSession;
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
    Optional<Consumer<InterfaceSession>> parseActions(JsonObject object);

    // parses all conditions
    @Contract(pure = true)
    Optional<Predicate<InterfaceSession>> parseConditions(JsonObject object);

    @Contract(value = "_, _, _ -> new", pure = true)
    Component renderText(Audience audience, String text, TagResolver... resolvers);
}
