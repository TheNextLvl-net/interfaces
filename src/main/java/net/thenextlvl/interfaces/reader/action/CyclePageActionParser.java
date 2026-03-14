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
        final var value = primitive.getAsString();
        return PageAction.changePage(parsePageValue(value)); // todo: use arithmetics with context
    }

    private static int parsePageValue(final String value) throws ParserException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new ParserException("Invalid change_page value '%s': expected 'next', 'previous', or a number (e.g. '+5', '-5', '9')", value);
        }
    }
}
