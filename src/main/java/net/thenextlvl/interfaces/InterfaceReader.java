package net.thenextlvl.interfaces;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.thenextlvl.interfaces.Arithmetics.compile;
import static net.thenextlvl.interfaces.Arithmetics.evaluate;

final class InterfaceReader {
    public static Interface read(final JsonObject object) {
        Preconditions.checkState(object.has("pattern"), "Missing pattern (json array)");

        final var title = object.has("title") ? Component.text(object.get("title").getAsString()) : null;
        final var pattern = object.getAsJsonArray("pattern").asList().stream()
                .map(JsonElement::getAsString)
                .toArray(String[]::new);

        final var builder = new SimpleInterface.Builder()
                .title(title);
        final var layout = Layout.builder()
                .pattern(pattern);

        for (final var entry : object.entrySet()) {
            if (entry.getKey().length() != 1) continue;
            final var jsonObject = entry.getValue().getAsJsonObject();
            final var renderer = readItemRenderer(jsonObject);
            final var actions = jsonObject.get("click_actions") instanceof final JsonArray array ? readActions(array) : null;
            if (actions == null) layout.mask(entry.getKey().charAt(0), renderer);
            else builder.slot(entry.getKey().charAt(0), new ActionItem(renderer, ClickAction.of(actions)));
        }

        final var onOpen = object.get("on_open") instanceof final JsonArray array ? readActions(array) : null;
        final var onClose = object.get("on_close") instanceof final JsonArray array ? readActions(array) : null;

        return builder
                .layout(layout.build())
                .onOpen(onOpen)
                .onClose(onClose != null ? (player, reason) -> onClose.accept(player) : null)
                .build();
    }

    private static Renderer readItemRenderer(final JsonObject object) {
        Preconditions.checkState(object.has("item"), "Missing item");
        final var item = object.get("item").getAsString();
        final var amount = object.get("amount") instanceof final JsonPrimitive primitive ? primitive.getAsString() : null;
        final var itemStack = Bukkit.getItemFactory().createItemStack(item);
        return amount != null ? context -> {
            itemStack.setAmount((int) evaluate(compile(amount, context)));
            return itemStack;
        } : context -> itemStack;
    }

    private static @Nullable Consumer<Player> readActions(final JsonArray array) {
        Consumer<Player> action = null;
        for (final var element : array) {
            final var read = element instanceof final JsonObject object ? readFullAction(object) : null;
            if (read != null) action = andOr(action, read);
        }
        return action;
    }

    private static @Nullable Consumer<Player> readFullAction(final JsonObject object) {
        final var permission = object.get("permission") instanceof final JsonPrimitive primitive ? primitive.getAsString() : null;
        final var noPermission = object.get("no_permission") instanceof final JsonPrimitive primitive ? primitive.getAsString() : null;

        final Predicate<Player> condition = player -> {
            if (permission != null && !player.hasPermission(permission)) return false;
            return noPermission == null || !player.hasPermission(noPermission);
        };

        final var action = readAction(object);
        return action == null ? null : player -> {
            if (condition.test(player)) action.accept(player);
        };
    }

    private static @Nullable Consumer<Player> readAction(final JsonObject object) {
        Consumer<Player> action = null;

        if (object.get("run_console_command") instanceof final JsonPrimitive primitive) {
            final var command = primitive.getAsString();
            action = player -> player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command.replace("<player>", player.getName()));
        }
        if (object.get("run_command") instanceof final JsonPrimitive primitive) {
            final var command = primitive.getAsString();
            action = andOr(action, player -> player.performCommand(command.replace("<player>", player.getName())));
        }
        if (object.get("broadcast") instanceof final JsonPrimitive primitive) {
            final var message = primitive.getAsString();
            action = andOr(action, player -> player.getServer().sendRichMessage(message, Placeholder.parsed("player", player.getName())));
        }
        if (object.get("send_message") instanceof final JsonPrimitive primitive) {
            final var message = primitive.getAsString();
            action = andOr(action, player -> player.sendRichMessage(message, Placeholder.parsed("player", player.getName())));
        }
        if (object.get("play_sound") instanceof final JsonObject soundObject) {
            Preconditions.checkState(soundObject.has("sound"), "Missing sound (key)");

            final var sound = soundObject.get("sound").getAsString();
            final var volume = soundObject.get("volume") instanceof final JsonPrimitive primitive ? primitive.getAsFloat() : 1f;
            final var pitch = soundObject.get("pitch") instanceof final JsonPrimitive primitive ? primitive.getAsFloat() : 1f;
            final var category = soundObject.get("category") instanceof final JsonPrimitive primitive
                    ? SoundCategory.valueOf(primitive.getAsString().toUpperCase()) : SoundCategory.MASTER;
            final var seed = soundObject.get("seed") instanceof final JsonPrimitive primitive ? primitive.getAsLong() : 0;
            action = andOr(action, player -> player.playSound(player.getLocation(), sound, category, volume, pitch, seed));
        }
        return action;
    }

    private static Consumer<Player> andOr(@Nullable final Consumer<Player> action, final Consumer<Player> other) {
        return action != null ? action.andThen(other) : other;
    }
}
