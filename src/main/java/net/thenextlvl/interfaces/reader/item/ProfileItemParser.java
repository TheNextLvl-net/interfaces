package net.thenextlvl.interfaces.reader.item;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonPrimitive;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.thenextlvl.interfaces.reader.ItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.function.Function;

public final class ProfileItemParser implements ItemParser<JsonPrimitive> {
    public static final ProfileItemParser INSTANCE = new ProfileItemParser();

    private ProfileItemParser() {
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Function<ItemStack, ItemStack> parse(final JsonPrimitive element, final ParserContext context) {
        final var profile = element.getAsString();
        final var builder = ResolvableProfile.resolvableProfile();
        if (profile.matches("^[!-~]{0,16}$")) builder.name(profile);
        else try {
            Base64.getDecoder().decode(profile);
            builder.addProperty(new ProfileProperty("textures", profile));
        } catch (final Exception ignored) {
        }
        final var resolved = builder.build();
        return itemStack -> {
            itemStack.setData(DataComponentTypes.PROFILE, resolved);
            return itemStack;
        };
    }
}
