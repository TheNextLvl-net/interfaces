package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class TransferActionParser implements ActionParser<JsonPrimitive> {
    public static final TransferActionParser INSTANCE = new TransferActionParser();

    private TransferActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        try {
            final var parts = primitive.getAsString().split(":", 2);
            final var host = parts[0];
            final var port = parts.length == 2 ? Integer.parseInt(parts[1]) : 25565;
            return session -> session.player().transfer(host, port);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port: " + primitive.getAsString(), e);
        }
    }
}
