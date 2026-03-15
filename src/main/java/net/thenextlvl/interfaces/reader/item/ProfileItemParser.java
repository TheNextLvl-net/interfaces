package net.thenextlvl.interfaces.reader.item;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonPrimitive;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.thenextlvl.interfaces.reader.ItemParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import net.thenextlvl.interfaces.reader.ParserException;
import org.bukkit.inventory.ItemStack;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Function;

public final class ProfileItemParser implements ItemParser<JsonPrimitive> {
    public static final ProfileItemParser INSTANCE = new ProfileItemParser();

    private ProfileItemParser() {
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Function<ItemStack, ItemStack> parse(final JsonPrimitive element, final ParserContext context) throws ParserException {
        final var profile = element.getAsString();
        final var builder = ResolvableProfile.resolvableProfile();
        if (profile.matches("^[!-~]{0,16}$")) builder.name(profile);
        else try {
            Base64.getDecoder().decode(profile);
            builder.addProperty(new ProfileProperty("textures", profile));
        } catch (final IllegalArgumentException ignored) {
            try {
                builder.uuid(UUID.fromString(profile));
            } catch (final IllegalArgumentException ignored2) {
                builder.addProperty(parseURL(profile));
            }
        }
        final var resolved = builder.build();
        return itemStack -> {
            itemStack.setData(DataComponentTypes.PROFILE, resolved);
            return itemStack;
        };
    }

    private static ProfileProperty parseURL(final String profile) throws ParserException {
        try {
            new URI(profile);
            final var texture = "{\"textures\":{\"SKIN\":{\"url\":\"" + profile + "\"}}}";
            final var base64 = Base64.getEncoder().encodeToString(texture.getBytes());
            return new ProfileProperty("textures", base64);
        } catch (final URISyntaxException e) {
            throw new ParserException("Invalid profile: %s", e, profile);
        }
    }
}
