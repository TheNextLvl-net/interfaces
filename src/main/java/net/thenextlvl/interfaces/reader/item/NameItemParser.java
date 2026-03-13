package net.thenextlvl.interfaces.reader.item;

import com.google.gson.JsonElement;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.reader.DynamicItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public final class NameItemParser implements DynamicItemParser<JsonElement> {
    public static final NameItemParser INSTANCE = new NameItemParser();

    private NameItemParser() {
    }

    @Override
    public BiFunction<ItemStack, RenderContext, ItemStack> parse(final JsonElement element, final ParserContext context) {
        return (itemStack, renderContext) -> {
            final var rendered = context.renderText(renderContext.player(), element);
            itemStack.setData(DataComponentTypes.ITEM_NAME, rendered);
            return itemStack;
        };
    }
}
