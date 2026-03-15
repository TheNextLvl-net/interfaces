package net.thenextlvl.interfaces.reader.item;

import com.google.gson.JsonArray;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.reader.DynamicItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public final class LoreItemParser implements DynamicItemParser<JsonArray> {
    public static final LoreItemParser INSTANCE = new LoreItemParser();

    private LoreItemParser() {
    }

    @Override
    public BiFunction<ItemStack, RenderContext, ItemStack> parse(final JsonArray element, final ParserContext context) {
        final var lines = element.asList();
        return (itemStack, renderContext) -> {
            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().lines(lines.stream()
                    .map(line -> context.renderText(renderContext.session().player(), line))
                    .toList()).build());
            return itemStack;
        };
    }
}
