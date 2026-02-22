package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.ClickAction;
import org.jetbrains.annotations.Contract;

@FunctionalInterface
public interface ClickActionParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    ClickAction parse(T element, ParserContext context);
}
