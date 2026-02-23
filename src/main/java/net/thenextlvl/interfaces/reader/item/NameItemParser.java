package net.thenextlvl.interfaces.reader.item;

import com.google.gson.JsonPrimitive;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.reader.DynamicItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public final class NameItemParser implements DynamicItemParser<JsonPrimitive> {
    public static final NameItemParser INSTANCE = new NameItemParser();

    private NameItemParser() {
    }

    @Override
    public BiConsumer<ItemStack, RenderContext> parse(final JsonPrimitive element, final ParserContext context) {
        return (itemStack, renderContext) -> {
            final var rendered = context.renderText(renderContext.player(), element);
            itemStack.setData(DataComponentTypes.ITEM_NAME, rendered);
        };
    }
}
