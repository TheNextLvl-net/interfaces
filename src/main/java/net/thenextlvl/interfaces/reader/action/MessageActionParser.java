package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
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
        return session -> session.player().sendMessage(context.renderText(session.player(), primitive));
    }
}
