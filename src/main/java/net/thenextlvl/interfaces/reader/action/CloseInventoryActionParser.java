package net.thenextlvl.interfaces.reader.action;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;

import java.util.function.Consumer;

public final class CloseInventoryActionParser implements ActionParser<JsonObject> {
    public static final CloseInventoryActionParser INSTANCE = new CloseInventoryActionParser();

    private CloseInventoryActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonObject object, final ParserContext context) {
        Preconditions.checkState(object.isEmpty(), "Inventory close action takes no arguments");
        return session -> session.player().closeInventory();
    }
}
