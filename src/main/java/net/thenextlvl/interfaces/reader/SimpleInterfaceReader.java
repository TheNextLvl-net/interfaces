package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.thenextlvl.interfaces.ActionItem;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.Interface;
import net.thenextlvl.interfaces.Layout;
import net.thenextlvl.interfaces.Renderer;
import net.thenextlvl.interfaces.reader.action.BroadcastActionParser;
import net.thenextlvl.interfaces.reader.action.CommandActionParser;
import net.thenextlvl.interfaces.reader.action.ConsoleCommandActionParser;
import net.thenextlvl.interfaces.reader.action.MessageActionParser;
import net.thenextlvl.interfaces.reader.action.SoundActionParser;
import net.thenextlvl.interfaces.reader.condition.NoPermissionConditionParser;
import net.thenextlvl.interfaces.reader.condition.PermissionConditionParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.thenextlvl.interfaces.reader.Arithmetics.compile;
import static net.thenextlvl.interfaces.reader.Arithmetics.evaluate;

final class SimpleInterfaceReader implements InterfaceReader, ParserContext {
    private final Set<RegisteredClickActionParser<?>> clickActionParsers = new CopyOnWriteArraySet<>();
    private final Set<RegisteredActionParser<?>> actionParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredActionParser<>("send_message", JsonPrimitive.class, MessageActionParser.INSTANCE),
            new RegisteredActionParser<>("broadcast", JsonPrimitive.class, BroadcastActionParser.INSTANCE),
            new RegisteredActionParser<>("run_command", JsonPrimitive.class, CommandActionParser.INSTANCE),
            new RegisteredActionParser<>("run_console_command", JsonPrimitive.class, ConsoleCommandActionParser.INSTANCE),
            new RegisteredActionParser<>("play_sound", JsonObject.class, SoundActionParser.INSTANCE)
    ));
    private final Set<RegisteredConditionParser<?>> conditionParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredConditionParser<>("permission", JsonPrimitive.class, PermissionConditionParser.INSTANCE),
            new RegisteredConditionParser<>("no_permission", JsonPrimitive.class, NoPermissionConditionParser.INSTANCE)
    ));

    private record RegisteredClickActionParser<T extends JsonElement>(
            String id,
            Class<T> type,
            ClickActionParser<T> parser
    ) {
    }

    private record RegisteredActionParser<T extends JsonElement>(
            String id,
            Class<T> type,
            ActionParser<T> parser
    ) {
    }

    private record RegisteredConditionParser<T extends JsonElement>(
            String id,
            Class<T> type,
            ConditionParser<T> parser
    ) {
    }

    @Override
    public <T extends JsonElement> InterfaceReader registerActionParser(final String id, final Class<T> type, final ClickActionParser<T> parser) {
        clickActionParsers.add(new RegisteredClickActionParser<>(id, type, parser));
        return this;
    }

    @Override
    public <T extends JsonElement> InterfaceReader registerActionParser(final String id, final Class<T> type, final ActionParser<T> parser) {
        actionParsers.add(new RegisteredActionParser<>(id, type, parser));
        return this;
    }

    @Override
    public <T extends JsonElement> InterfaceReader registerConditionParser(final String id, final Class<T> type, final ConditionParser<T> parser) {
        conditionParsers.add(new RegisteredConditionParser<>(id, type, parser));
        return this;
    }

    @Override
    public Interface read(final Path path) throws IOException {
        try (final var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    @Override
    public Interface read(final Reader reader) {
        return read(JsonParser.parseReader(reader).getAsJsonObject());
    }

    @Override
    public Interface read(final InputStream input) throws IOException {
        try (final var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    @Override
    public Interface read(final JsonObject object) throws IllegalStateException {
        final var pattern = get(object, "pattern", JsonArray.class).map(list ->
                list.asList().stream().map(JsonElement::getAsString).toArray(String[]::new)
        ).orElseThrow(() -> new IllegalStateException("Missing or invalid pattern"));

        final var builder = Interface.builder();

        get(object, "title", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .map(Component::text)
                .ifPresent(builder::title);

        final var layout = Layout.builder()
                .pattern(pattern);

        for (final var entry : object.entrySet()) {
            if (entry.getKey().length() != 1) continue;
            if (!entry.getValue().isJsonObject())
                throw new IllegalStateException("Invalid entry \"" + entry.getKey() + "\": " + entry.getValue());
            final var jsonObject = entry.getValue().getAsJsonObject();
            final var renderer = readItemRenderer(jsonObject);
            get(jsonObject, "click_actions", JsonArray.class)
                    .flatMap(this::readClickActions)
                    .ifPresentOrElse(actions -> {
                        builder.slot(entry.getKey().charAt(0), new ActionItem(renderer, actions));
                    }, () -> {
                        layout.mask(entry.getKey().charAt(0), renderer);
                    });
        }

        get(object, "on_open", JsonArray.class)
                .flatMap(this::readActions)
                .ifPresent(builder::onOpen);
        get(object, "on_close", JsonArray.class)
                .flatMap(this::readActions)
                .map(actions -> (BiConsumer<Player, Reason>) (player, reason) -> actions.accept(player))
                .ifPresent(builder::onClose);

        return builder.layout(layout.build()).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ClickAction> parseClickActions(final JsonObject object) {
        final Function<RegisteredClickActionParser<?>, @Nullable ClickAction> function = p -> {
            final var parser = (RegisteredClickActionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return clickActionParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(ClickAction::andThen)
                .map(clickAction -> parseActions(object)
                        .map(ClickAction::of)
                        .map(clickAction::andThen)
                        .orElse(clickAction))
                .or(() -> parseActions(object)
                        .map(ClickAction::of));
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public Optional<Consumer<Player>> parseActions(final JsonObject object) {
        final Function<RegisteredActionParser<?>, @Nullable Consumer<Player>> function = p -> {
            final var parser = (RegisteredActionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return actionParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen);
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public Optional<Predicate<Player>> parseConditions(final JsonObject object) {
        final Function<RegisteredConditionParser<?>, @Nullable Predicate<Player>> function = p -> {
            final var parser = (RegisteredConditionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return conditionParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(Predicate::and);
    }

    private <T extends JsonElement> Optional<T> get(final JsonObject object, final String key, final Class<T> type) {
        return Optional.ofNullable(object.get(key)).filter(type::isInstance).map(type::cast);
    }

    private Renderer readItemRenderer(final JsonObject object) {
        final var item = get(object, "item", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IllegalStateException("Missing or invalid item"));
        final var amount = get(object, "amount", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .orElse(null);
        final var itemStack = Bukkit.getItemFactory().createItemStack(item);
        return amount != null ? context -> {
            final var clone = itemStack.clone();
            clone.setAmount((int) evaluate(compile(amount, context)));
            return clone;
        } : context -> itemStack.clone();
    }

    private Optional<ClickAction> readClickActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> parseClickActions(object).orElse(null))
                .filter(Objects::nonNull)
                .reduce(ClickAction::andThen)
                .map(actions -> readConditions(array).map(conditions -> (ClickAction) (player, type, index) -> {
                    if (conditions.test(player)) actions.click(player, type, index);
                }).orElse(actions));
    }

    private Optional<Consumer<Player>> readActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> parseActions(object).orElse(null))
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .map(actions -> readConditions(array).map(conditions -> (Consumer<Player>) player -> {
                    if (conditions.test(player)) actions.accept(player);
                }).orElse(actions));
    }

    private Optional<Predicate<Player>> readConditions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> parseConditions(object).orElse(null))
                .filter(Objects::nonNull)
                .reduce(Predicate::and);
    }
}
