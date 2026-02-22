package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class CommandActionParser implements ActionParser<JsonPrimitive> {
    public static final CommandActionParser INSTANCE = new CommandActionParser();

    private CommandActionParser() {
    }

    @Override
    public Consumer<Player> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var command = primitive.getAsString();
        return player -> player.performCommand(command.replace("<player>", player.getName()));
    }
}
