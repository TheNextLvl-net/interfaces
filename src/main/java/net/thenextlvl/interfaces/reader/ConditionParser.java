package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonElement;
import net.thenextlvl.interfaces.InterfaceSession;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;

@FunctionalInterface
public interface ConditionParser<T extends JsonElement> {
    @Contract(value = "_, _ -> new", pure = true)
    Predicate<InterfaceSession> parse(T element, ParserContext context);
}
