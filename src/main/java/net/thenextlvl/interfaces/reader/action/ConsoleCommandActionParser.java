package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class ConsoleCommandActionParser implements ActionParser<JsonPrimitive> {
    public static final ConsoleCommandActionParser INSTANCE = new ConsoleCommandActionParser();

    private ConsoleCommandActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var command = primitive.getAsString();
        return session -> session.player().getServer().dispatchCommand(
                session.player().getServer().getConsoleSender(),
                command.replace("<player>", session.player().getName())
        );
    }
}
