package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class BroadcastActionParser implements ActionParser<JsonPrimitive> {
    public static final BroadcastActionParser INSTANCE = new BroadcastActionParser();

    private BroadcastActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var message = primitive.getAsString();
        return session -> {
            final var player = Placeholder.parsed("player", session.player().getName());
            session.player().getServer().forEachAudience(audience -> {
                audience.sendMessage(context.renderText(audience, message, player));
            });
        };
    }
}
