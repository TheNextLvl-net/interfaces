package net.thenextlvl.interfaces.reader.action;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonPrimitive;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.reader.ActionParser;
import net.thenextlvl.interfaces.reader.ParserContext;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public final class ConnectActionParser implements ActionParser<JsonPrimitive> {
    public static final ConnectActionParser INSTANCE = new ConnectActionParser();
    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(ConnectActionParser.class);

    private ConnectActionParser() {
    }

    @Override
    public Consumer<InterfaceSession> parse(final JsonPrimitive primitive, final ParserContext context) {
        final var dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(primitive.getAsString());
        final var bytes = dataOutput.toByteArray();
        return session -> session.player().sendPluginMessage(plugin, "BungeeCord", bytes);
    }

    static {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }
}
