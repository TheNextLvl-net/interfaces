package net.thenextlvl.interfaces.reader.item;

import com.google.gson.JsonPrimitive;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.thenextlvl.interfaces.reader.ItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public final class HideTooltipItemParser implements ItemParser<JsonPrimitive> {
    public static final HideTooltipItemParser INSTANCE = new HideTooltipItemParser();

    private HideTooltipItemParser() {
    }

    @Override
    public Function<ItemStack, ItemStack> parse(final JsonPrimitive element, final ParserContext context) {
        final var hide = element.getAsBoolean();
        final var display = TooltipDisplay.tooltipDisplay().hideTooltip(hide);
        return itemStack -> {
            itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);
            return itemStack;
        };
    }
}
