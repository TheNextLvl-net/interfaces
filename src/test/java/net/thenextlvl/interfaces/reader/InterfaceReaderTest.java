package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class InterfaceReaderTest {
    @Test
    void parseActionsReturnsEmptyWhenNoParserMatches() {
        final var context = (ParserContext) InterfaceReader.reader();
        final var object = parseObject("""
                {"unknown":"value"}
                """);

        assertTrue(context.parseActions(object).isEmpty());
    }

    @Test
    void parseConditionsCombinesRegisteredConditionsWithAnd() {
        final var reader = (ParserContext) InterfaceReader.reader()
                .registerConditionParser("first", JsonPrimitive.class, (primitive, context) -> player -> primitive.getAsBoolean())
                .registerConditionParser("second", JsonPrimitive.class, (primitive, context) -> player -> primitive.getAsBoolean());

        final var allow = parseObject("""
                {"first":true,"second":true}
                """);
        final var deny = parseObject("""
                {"first":true,"second":false}
                """);

        final var player = mock(Player.class);
        assertTrue(reader.parseConditions(allow).orElseThrow().test(player));
        assertFalse(reader.parseConditions(deny).orElseThrow().test(player));
    }

    @Test
    void parseClickActionsCombinesClickActionsAndFallbackActions() {
        final var clicks = new AtomicInteger();
        final var actions = new AtomicInteger();

        final ClickActionParser<JsonPrimitive> clickParser = (primitive, context) -> (player, type, index) -> clicks.incrementAndGet();
        final ActionParser<JsonPrimitive> actionParser = (primitive, context) -> player -> actions.incrementAndGet();
        final var reader = (ParserContext) InterfaceReader.reader()
                .registerActionParser("custom_click", JsonPrimitive.class, clickParser)
                .registerActionParser("custom_action", JsonPrimitive.class, actionParser);

        final var object = parseObject("""
                {"custom_click":true,"custom_action":"run"}
                """);

        reader.parseClickActions(object).orElseThrow().click(mock(Player.class), ClickType.LEFT, 0);
        assertEquals(1, clicks.get());
        assertEquals(1, actions.get());
    }

    @Test
    void parseClickActionsFallsBackToRegularActionsWhenNoClickActionMatches() {
        final var executed = new AtomicBoolean(false);
        final ActionParser<JsonPrimitive> actionParser = (primitive, context) -> player -> executed.set(true);
        final var reader = (ParserContext) InterfaceReader.reader()
                .registerActionParser("custom_action", JsonPrimitive.class, actionParser);

        final var object = parseObject("""
                {"custom_action":"run"}
                """);

        reader.parseClickActions(object).orElseThrow().click(mock(Player.class), ClickType.RIGHT, 2);
        assertTrue(executed.get());
    }

    @Test
    void readThrowsWhenPatternIsMissingOrInvalid() {
        final var reader = InterfaceReader.reader();

        assertThrows(IllegalStateException.class, () -> reader.read(parseObject("{}")));
        assertThrows(IllegalStateException.class, () -> reader.read(parseObject("{\"pattern\":1}")));
    }

    private static JsonObject parseObject(final String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
