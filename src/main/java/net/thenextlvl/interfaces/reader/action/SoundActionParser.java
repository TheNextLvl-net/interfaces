package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.SoundCategory;

import java.util.function.Consumer;

public final class SoundActionParser implements ActionParser<JsonObject> {
    public static final SoundActionParser INSTANCE = new SoundActionParser();

    private SoundActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonObject object, final ParserContext context) {
        final var sound = object.get("sound").getAsString();
        final var volume = object.get("volume") instanceof final JsonPrimitive primitive ? primitive.getAsFloat() : 1f;
        final var pitch = object.get("pitch") instanceof final JsonPrimitive primitive ? primitive.getAsFloat() : 1f;
        final var category = object.get("category") instanceof final JsonPrimitive primitive
                ? SoundCategory.valueOf(primitive.getAsString().toUpperCase()) : SoundCategory.MASTER;
        final var seed = object.get("seed") instanceof final JsonPrimitive primitive ? primitive.getAsLong() : 0;

        return session -> session.player().playSound(session.player().getLocation(), sound, category, volume, pitch, seed);
    }
}
