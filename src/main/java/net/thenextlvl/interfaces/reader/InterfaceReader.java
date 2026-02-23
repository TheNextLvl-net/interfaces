package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.Interface;
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
    Interface read(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(Reader reader) throws JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(JsonObject object) throws IllegalStateException;

    @FunctionalInterface
    interface TextRenderer {
        @Contract(value = "_, _, _ -> new", pure = true)
        Component renderText(JsonElement element, Audience audience, TagResolver... resolvers);
    }
}
