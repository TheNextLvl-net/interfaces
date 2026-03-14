package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.Interface;
import net.thenextlvl.interfaces.PaginatedInterface;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

public interface InterfaceReader extends ParserContext {
    @Contract(value = " -> new", pure = true)
    static InterfaceReader reader() {
        return new SimpleInterfaceReader();
    }

    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerActionParser(String id, Class<T> type, ClickActionParser<T> parser);

    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerActionParser(String id, Class<T> type, ActionParser<T> parser);

    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerConditionParser(String id, Class<T> type, ConditionParser<T> parser);

    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerItemParser(String id, Class<T> type, ItemParser<T> parser);

    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerDynamicItemParser(String id, Class<T> type, DynamicItemParser<T> parser);

    @Contract(value = "_ -> this", mutates = "this")
    InterfaceReader textRenderer(TextRenderer renderer);

    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(Reader reader) throws JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(JsonObject object) throws IllegalStateException;

    /**
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder readResource(String path) throws IOException;

    @Contract(value = "_ -> new", pure = true)
    PaginatedInterface.Builder<?> readPaginated(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    PaginatedInterface.Builder<?> readPaginated(Reader reader) throws JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    PaginatedInterface.Builder<?> readPaginated(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    @Contract(value = "_ -> new", pure = true)
    PaginatedInterface.Builder<?> readPaginated(JsonObject object) throws IllegalStateException;

    @Contract(value = "_ -> new", pure = true)
    PaginatedInterface.Builder<?> readPaginatedResource(String path) throws IOException;

    @FunctionalInterface
    interface TextRenderer {
        @Contract(value = "_, _, _ -> new", pure = true)
        Component renderText(String text, Audience audience, TagResolver... resolvers) throws ParserException;

        @Contract(value = "_, _, _ -> new", pure = true)
        default Component renderText(final JsonElement element, final Audience audience, final TagResolver... resolvers) throws ParserException {
            if (element.isJsonObject()) return renderText(element.getAsJsonObject(), audience, resolvers);
            return renderText(element.getAsString(), audience, resolvers);
        }

        @Contract(value = "_, _, _ -> new", pure = true)
        default Component renderText(final JsonObject object, final Audience audience, final TagResolver... resolvers) throws ParserException {
            final var text = ParserConditions.checkNonNull(object.get("content"), "Text 'content' is missing").getAsString();
            return renderText(text, audience, resolveTags(object).resolvers(resolvers).build());
        }

        @SuppressWarnings("PatternValidation")
        default TagResolver.Builder resolveTags(final JsonObject object) {
            final var builder = TagResolver.builder();

            for (final var entry : object.entrySet()) {
                if (entry.getKey().equals("content")) continue;
                builder.tag(entry.getKey(), Tag.preProcessParsed(entry.getValue().getAsString()));
            }

            return builder;
        }
    }
}
