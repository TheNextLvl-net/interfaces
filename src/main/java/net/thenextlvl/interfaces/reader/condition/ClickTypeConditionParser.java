package net.thenextlvl.interfaces.reader.condition;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.ClickContext;
import net.thenextlvl.interfaces.reader.ClickConditionParser;
import net.thenextlvl.interfaces.reader.ParserConditions;
import net.thenextlvl.interfaces.reader.ParserContext;
import net.thenextlvl.interfaces.reader.ParserException;
import org.bukkit.event.inventory.ClickType;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class ClickTypeConditionParser implements ClickConditionParser<JsonPrimitive> {
    public static final ClickTypeConditionParser INSTANCE = new ClickTypeConditionParser();

    private static final Map<String, Predicate<ClickType>> clickTypes = Map.ofEntries(
            Map.entry("any", clickType -> true),
            Map.entry("any_left", ClickType::isLeftClick),
            Map.entry("any_right", ClickType::isRightClick),
            Map.entry("left", clickType -> clickType == ClickType.LEFT),
            Map.entry("right", clickType -> clickType == ClickType.RIGHT),
            Map.entry("any_shift", ClickType::isShiftClick),
            Map.entry("shift_left", clickType -> clickType.isLeftClick() && clickType.isShiftClick()),
            Map.entry("shift_right", clickType -> clickType.isRightClick() && clickType.isShiftClick()),
            Map.entry("keyboard", ClickType::isKeyboardClick),
            Map.entry("mouse", ClickType::isMouseClick),
            Map.entry("double_click", clickType -> clickType == ClickType.DOUBLE_CLICK),
            Map.entry("any_drop", clickType -> clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP),
            Map.entry("drop", clickType -> clickType == ClickType.DROP),
            Map.entry("drop_all", clickType -> clickType == ClickType.CONTROL_DROP),
            Map.entry("creative", ClickType::isCreativeAction),
            Map.entry("swap_offhand", clickType -> clickType == ClickType.SWAP_OFFHAND),
            Map.entry("unknown", clickType -> clickType == ClickType.UNKNOWN),
            Map.entry("border_left", clickType -> clickType == ClickType.WINDOW_BORDER_LEFT),
            Map.entry("border_right", clickType -> clickType == ClickType.WINDOW_BORDER_RIGHT),
            Map.entry("middle", clickType -> clickType == ClickType.MIDDLE)
    );

    private ClickTypeConditionParser() {
    }

    @Override
    public Predicate<ClickContext> parse(final JsonPrimitive element, final ParserContext context) throws ParserException {
        final var value = element.getAsString()
                .toLowerCase(Locale.ROOT)
                .replace("-", "_")
                .strip();
        final var invert = value.startsWith("!");

        final var key = invert ? value.substring(1) : value;
        ParserConditions.checkState(!invert || !key.equals("any"), "Cannot invert 'any' click type");

        final var type = Optional.ofNullable(clickTypes.get(key))
                .map(clickTypePredicate -> invert ? clickTypePredicate.negate() : clickTypePredicate)
                .orElseThrow(() -> new ParserException("Unknown click type: %s", key));

        return clickContext -> type.test(clickContext.clickType());
    }
}
