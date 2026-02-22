package net.thenextlvl.interfaces.reader.condition;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.reader.ConditionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public final class PermissionConditionParser implements ConditionParser<JsonPrimitive> {
    public static final PermissionConditionParser INSTANCE = new PermissionConditionParser();

    private PermissionConditionParser() {
    }

    @Override
    public Predicate<Player> parse(final JsonPrimitive element, final ParserContext context) {
        final var permission = element.getAsString();
        return player -> player.hasPermission(permission);
    }
}
