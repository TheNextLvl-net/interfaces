package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.reader.ArithmeticsParser;
import net.thenextlvl.interfaces.reader.ClickActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import net.thenextlvl.interfaces.reader.ParserException;

public final class SetPageActionParser implements ClickActionParser<JsonPrimitive> {
    public static final SetPageActionParser INSTANCE = new SetPageActionParser();

    private SetPageActionParser() {
    }

    @Override
    public ClickAction parse(final JsonPrimitive primitive, final ParserContext context) throws ParserException {
        final var expression = primitive.getAsString();
        return clickContext -> clickContext.paginatedSession().ifPresent(session -> {
            final var value = (int) ArithmeticsParser.parser().evaluate(expression, clickContext);
            session.page(Math.clamp(value, 0, session.pageCount() - 1));
        });
    }
}
