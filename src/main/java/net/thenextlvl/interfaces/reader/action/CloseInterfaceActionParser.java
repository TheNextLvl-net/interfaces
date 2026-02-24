package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonObject;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserConditions;
import net.thenextlvl.interfaces.reader.ParserContext;
import net.thenextlvl.interfaces.reader.ParserException;

import java.util.function.Consumer;

public final class CloseInterfaceActionParser implements ActionParser<JsonObject> {
    public static final CloseInterfaceActionParser INSTANCE = new CloseInterfaceActionParser();

    private CloseInterfaceActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonObject object, final ParserContext context) throws ParserException {
        ParserConditions.checkState(object.isEmpty(), "Interface close action takes no arguments");
        return session -> session.player().closeInventory();
    }
}
