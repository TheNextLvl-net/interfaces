package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.Interface;
import net.thenextlvl.interfaces.PaginatedInterface;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

/**
 * Reads interfaces from JSON files.
 *
 * @since 0.2.0
 */
public interface InterfaceReader extends ParserContext {
    /**
     * Creates a new interface reader for a given plugin.
     *
     * @param plugin The plugin to create the reader for
     * @return The interface reader
     * @since 0.4.0
     */
    @Contract(value = "_ -> new", pure = true)
    static InterfaceReader reader(final JavaPlugin plugin) {
        return new SimpleInterfaceReader(plugin);
    }

    /**
     * Creates a new interface reader for the plugin that provides this class.
     *
     * @return The interface reader
     * @see JavaPlugin#getProvidingPlugin(Class)
     * @see #reader(JavaPlugin)
     * @since 0.2.0
     */
    @Contract(value = " -> new", pure = true)
    static InterfaceReader reader() {
        return reader(JavaPlugin.getProvidingPlugin(InterfaceReader.class));
    }

    /**
     * Registers an action parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The action parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerActionParser(String id, Class<T> type, ActionParser<T> parser);

    /**
     * Registers a click action parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The click action parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerClickActionParser(String id, Class<T> type, ClickActionParser<T> parser);

    /**
     * Registers a condition parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The condition parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerConditionParser(String id, Class<T> type, ConditionParser<T> parser);

    /**
     * Registers a click condition parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The click condition parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.4.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerClickConditionParser(String id, Class<T> type, ClickConditionParser<T> parser);

    /**
     * Registers an item parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The item parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerItemParser(String id, Class<T> type, ItemParser<T> parser);

    /**
     * Registers a dynamic item parser.
     *
     * @param id     The parser ID
     * @param type   The type of the JSON element
     * @param parser The dynamic item parser
     * @param <T>    The type of the JSON element
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_, _, _ -> this", mutates = "this")
    <T extends JsonElement> InterfaceReader registerDynamicItemParser(String id, Class<T> type, DynamicItemParser<T> parser);

    /**
     * Sets the text renderer.
     *
     * @param renderer The text renderer
     * @return The interface reader
     * @since 0.2.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    InterfaceReader textRenderer(TextRenderer renderer);

    /**
     * Reads an interface from a JSON file.
     *
     * @param path The path to the JSON file
     * @return The interface builder
     * @throws IOException         If the file could not be read
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    /**
     * Reads an interface from a JSON reader.
     *
     * @param reader The JSON reader
     * @return The interface builder
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(Reader reader) throws JsonIOException, JsonSyntaxException;

    /**
     * Reads an interface from an input stream.
     *
     * @param input The input stream
     * @return The interface builder
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @throws IOException         If an I/O error occurs
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    /**
     * Reads an interface from a JSON object.
     *
     * @param object The JSON object
     * @return The interface builder
     * @throws IllegalStateException If the JSON object is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder read(JsonObject object) throws IllegalStateException;

    /**
     * Reads an interface from a resource file.
     *
     * @param path The path to the resource file
     * @return The interface builder
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the resource file could not be found
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    Interface.Builder readResource(String path) throws IOException, NullPointerException;

    /**
     * Reads a paginated interface from a resource file.
     *
     * @param path The path to the resource file
     * @return The paginated interface builder
     * @throws IOException         If an I/O error occurs
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PaginatedInterface.Builder<T> readPaginated(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    /**
     * Reads a paginated interface from a JSON reader.
     *
     * @param reader The JSON reader
     * @param <T>    The type of the entries
     * @return The paginated interface builder
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PaginatedInterface.Builder<T> readPaginated(Reader reader) throws JsonIOException, JsonSyntaxException;

    /**
     * Reads a paginated interface from an input stream.
     *
     * @param input The input stream
     * @param <T>   The type of the entries
     * @return The paginated interface builder
     * @throws JsonIOException     If the JSON file could not be read
     * @throws JsonSyntaxException If the JSON file is invalid
     * @throws IOException         If an I/O error occurs
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PaginatedInterface.Builder<T> readPaginated(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    /**
     * Reads a paginated interface from a JSON object.
     *
     * @param object The JSON object
     * @param <T>    The type of the entries
     * @return The paginated interface builder
     * @throws IllegalStateException If the JSON object is invalid
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PaginatedInterface.Builder<T> readPaginated(JsonObject object) throws IllegalStateException;

    /**
     * Reads a paginated interface from a resource file.
     *
     * @param path The path to the resource file
     * @param <T>  The type of the entries
     * @return The paginated interface builder
     * @throws IOException If an I/O error occurs
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    <T> PaginatedInterface.Builder<T> readPaginatedResource(String path) throws IOException;

    /**
     * Renders text using a text renderer.
     *
     * @since 0.2.0
     */
    @FunctionalInterface
    interface TextRenderer {
        /**
         * Renders text.
         *
         * @param text      The text
         * @param audience  The audience
         * @param resolvers The tag resolvers
         * @return The rendered text
         * @since 0.2.0
         */
        @Contract(value = "_, _, _ -> new", pure = true)
        Component renderText(String text, Audience audience, TagResolver... resolvers);
    }
}
