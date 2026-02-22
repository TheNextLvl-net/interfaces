package net.thenextlvl.interfaces.reader.action;

import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class MessageActionParser implements ActionParser<JsonPrimitive> {
    public static final MessageActionParser INSTANCE = new MessageActionParser();

    private MessageActionParser() {
    }

    @Override
    public Consumer<Player> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var message = primitive.getAsString();
        return player -> player.sendRichMessage(message,
                Placeholder.parsed("player", player.getName()));
    }
}
