package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.ClickContext;
import net.thenextlvl.interfaces.InterfaceSession;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A parser context for parsing JSON elements.
 *
 * @since 0.2.0
 */
public interface ParserContext {
    /**
     * Parses all click actions for a given JSON object.
     *
     * @param object The JSON object
     * @return The parsed click actions
     * @since 0.2.0
     */
    @Contract(pure = true)
    Optional<ClickAction> parseClickActions(JsonObject object);

    /**
     * Parses all actions for a given JSON object.
     *
     * @param object The JSON object
     * @return The parsed actions
     * @since 0.2.0
     */
    @Contract(pure = true)
    Optional<Consumer<InterfaceSession>> parseActions(JsonObject object);

    /**
     * Parses all conditions for a given JSON object.
     *
     * @param object The JSON object
     * @return The parsed conditions
     * @since 0.2.0
     */
    @Contract(pure = true)
    Optional<Predicate<InterfaceSession>> parseConditions(JsonObject object);

    /**
     * Parses all click-specific conditions for a given JSON object.
     *
     * @param object The JSON object
     * @return The parsed click-specific conditions
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<Predicate<ClickContext>> parseClickConditions(JsonObject object);

    /**
     * Renders a text element for a given audience.
     *
     * @param audience  The audience
     * @param element   The text element
     * @param resolvers The tag resolvers
     * @return The rendered text
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    Component renderText(Audience audience, JsonElement element, TagResolver... resolvers);

    /**
     * Renders a text string for a given audience.
     *
     * @param audience  The audience
     * @param text      The text string
     * @param resolvers The tag resolvers
     * @return The rendered text
     * @since 0.3.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    Component renderText(Audience audience, String text, TagResolver... resolvers);
}
