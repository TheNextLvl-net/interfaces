package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.PageAction;
import net.thenextlvl.interfaces.reader.ClickActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import net.thenextlvl.interfaces.reader.ParserException;

public final class CyclePageActionParser implements ClickActionParser<JsonPrimitive> {
    public static final CyclePageActionParser INSTANCE = new CyclePageActionParser();

    private CyclePageActionParser() {
    }

    @Override
    public ClickAction parse(final JsonPrimitive primitive, final ParserContext context) throws ParserException {
        // todo: use arithmetics with context
        final var value = primitive.getAsInt();
        return PageAction.changePage(value);
    }
}
