package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class MessageActionParser implements ActionParser<JsonPrimitive> {
    public static final MessageActionParser INSTANCE = new MessageActionParser();

    private MessageActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var message = primitive.getAsString();
        return session -> session.player().sendRichMessage(message,
                Placeholder.parsed("player", session.player().getName()));
    }
}
