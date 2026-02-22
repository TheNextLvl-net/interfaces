package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.thenextlvl.interfaces.Interface;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

public interface InterfaceReader {
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

    @Contract(value = "_ -> new", pure = true)
    Interface read(Path path) throws IOException, JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(Reader reader) throws JsonIOException, JsonSyntaxException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(InputStream input) throws JsonIOException, JsonSyntaxException, IOException;

    @Contract(value = "_ -> new", pure = true)
    Interface read(JsonObject object) throws IllegalStateException;
}
