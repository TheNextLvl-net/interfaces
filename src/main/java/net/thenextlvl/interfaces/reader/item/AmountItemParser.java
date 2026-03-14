package net.thenextlvl.interfaces.reader.item;

import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.reader.ArithmeticsParser;
import net.thenextlvl.interfaces.reader.DynamicItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public final class AmountItemParser implements DynamicItemParser<JsonPrimitive> {
    public static final AmountItemParser INSTANCE = new AmountItemParser();

    private AmountItemParser() {
    }

    @Override
    public BiFunction<ItemStack, RenderContext, ItemStack> parse(final JsonPrimitive element, final ParserContext context) {
        final var expression = element.getAsString();
        return (itemStack, renderContext) -> {
            itemStack.setAmount((int) ArithmeticsParser.parser().evaluate(expression, renderContext)); 
            return itemStack;
        };
    }
}
