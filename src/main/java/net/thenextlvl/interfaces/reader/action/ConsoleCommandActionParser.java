package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class ConsoleCommandActionParser implements ActionParser<JsonPrimitive> {
    public static final ConsoleCommandActionParser INSTANCE = new ConsoleCommandActionParser();

    private ConsoleCommandActionParser() {
    }

    @Override
    public Consumer<Player> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var command = primitive.getAsString();
        return player -> player.getServer().dispatchCommand(
                player.getServer().getConsoleSender(),
                command.replace("<player>", player.getName())
        );
    }
}
