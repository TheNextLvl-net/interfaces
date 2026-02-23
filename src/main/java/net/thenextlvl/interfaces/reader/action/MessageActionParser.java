package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class MessageActionParser implements ActionParser<JsonElement> {
    public static final MessageActionParser INSTANCE = new MessageActionParser();

    private MessageActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonElement primitive, final ParserContext context) {
        return session -> session.player().sendMessage(context.renderText(session.player(), primitive,
                Placeholder.parsed("player", session.player().getName())));
    }
}
