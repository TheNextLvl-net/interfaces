package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class CommandActionParser implements ActionParser<JsonPrimitive> {
    public static final CommandActionParser INSTANCE = new CommandActionParser();

    private CommandActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var command = primitive.getAsString();
        return session -> session.player().performCommand(command.replace("<player>", session.player().getName()));
    }
}
