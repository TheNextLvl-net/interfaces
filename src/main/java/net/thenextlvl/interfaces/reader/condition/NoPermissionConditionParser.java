package net.thenextlvl.interfaces.reader.condition;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ConditionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Predicate;

public final class NoPermissionConditionParser implements ConditionParser<JsonPrimitive> {
    public static final NoPermissionConditionParser INSTANCE = new NoPermissionConditionParser();

    private NoPermissionConditionParser() {
    }

    @Override
    public Predicate<InterfaceSession> parse(final JsonPrimitive element, final ParserContext context) {
        final var permission = element.getAsString();
        return session -> !session.getPlayer().hasPermission(permission);
    }
}
