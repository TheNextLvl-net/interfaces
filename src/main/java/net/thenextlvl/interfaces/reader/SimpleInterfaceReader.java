package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.ActionItem;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.Interface;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.Layout;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.Renderer;
import net.thenextlvl.interfaces.reader.action.BroadcastActionParser;
import net.thenextlvl.interfaces.reader.action.CloseInventoryActionParser;
import net.thenextlvl.interfaces.reader.action.CommandActionParser;
import net.thenextlvl.interfaces.reader.action.ConnectActionParser;
import net.thenextlvl.interfaces.reader.action.ConsoleCommandActionParser;
import net.thenextlvl.interfaces.reader.action.MessageActionParser;
import net.thenextlvl.interfaces.reader.action.SoundActionParser;
import net.thenextlvl.interfaces.reader.action.TransferActionParser;
import net.thenextlvl.interfaces.reader.condition.ClickTypeConditionParser;
import net.thenextlvl.interfaces.reader.condition.NoPermissionConditionParser;
import net.thenextlvl.interfaces.reader.condition.PermissionConditionParser;
import net.thenextlvl.interfaces.reader.item.AmountItemParser;
import net.thenextlvl.interfaces.reader.item.HideTooltipItemParser;
import net.thenextlvl.interfaces.reader.item.LoreItemParser;
import net.thenextlvl.interfaces.reader.item.NameItemParser;
import net.thenextlvl.interfaces.reader.item.ProfileItemParser;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.ItemStack;
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

final class SimpleInterfaceReader implements InterfaceReader, ParserContext {
    private final Set<RegisteredClickActionParser<?>> clickActionParsers = new CopyOnWriteArraySet<>();
    private final Set<RegisteredActionParser<?>> actionParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredActionParser<>("send_message", JsonPrimitive.class, MessageActionParser.INSTANCE),
            new RegisteredActionParser<>("broadcast", JsonPrimitive.class, BroadcastActionParser.INSTANCE),
            new RegisteredActionParser<>("run_command", JsonPrimitive.class, CommandActionParser.INSTANCE),
            new RegisteredActionParser<>("run_console_command", JsonPrimitive.class, ConsoleCommandActionParser.INSTANCE),
            new RegisteredActionParser<>("play_sound", JsonObject.class, SoundActionParser.INSTANCE),
            new RegisteredActionParser<>("transfer", JsonPrimitive.class, TransferActionParser.INSTANCE),
            new RegisteredActionParser<>("connect", JsonPrimitive.class, ConnectActionParser.INSTANCE),
            new RegisteredActionParser<>("close_inventory", JsonObject.class, CloseInventoryActionParser.INSTANCE)
    ));
    private final Set<RegisteredConditionParser<?>> conditionParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredConditionParser<>("click_type", JsonPrimitive.class, ClickTypeConditionParser.INSTANCE),
            new RegisteredConditionParser<>("permission", JsonPrimitive.class, PermissionConditionParser.INSTANCE),
            new RegisteredConditionParser<>("no_permission", JsonPrimitive.class, NoPermissionConditionParser.INSTANCE)
    ));
    private final Set<RegisteredItemParser<?>> itemParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredItemParser<>("profile", JsonPrimitive.class, ProfileItemParser.INSTANCE),
            new RegisteredItemParser<>("hide_tooltip", JsonPrimitive.class, HideTooltipItemParser.INSTANCE)
    ));
    private final Set<RegisteredDynamicItemParser<?>> dynamicItemParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredDynamicItemParser<>("amount", JsonPrimitive.class, AmountItemParser.INSTANCE),
            new RegisteredDynamicItemParser<>("name", JsonPrimitive.class, NameItemParser.INSTANCE),
            new RegisteredDynamicItemParser<>("lore", JsonArray.class, LoreItemParser.INSTANCE)
    ));
    private TextRenderer renderer = (text, audience, resolvers) -> {
        var builder = TagResolver.builder()
                .resolvers(resolvers);
        return MiniMessage.miniMessage().deserialize(text, builder.build());
    };

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

    private record RegisteredItemParser<T extends JsonElement>(
            String id,
            Class<T> type,
            ItemParser<T> parser
    ) {
    }

    private record RegisteredDynamicItemParser<T extends JsonElement>(
            String id,
            Class<T> type,
            DynamicItemParser<T> parser
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
    public <T extends JsonElement> InterfaceReader registerItemParser(final String id, final Class<T> type, final ItemParser<T> parser) {
        itemParsers.add(new RegisteredItemParser<>(id, type, parser));
        return this;
    }

    @Override
    public <T extends JsonElement> InterfaceReader registerDynamicItemParser(final String id, final Class<T> type, final DynamicItemParser<T> parser) {
        dynamicItemParsers.add(new RegisteredDynamicItemParser<>(id, type, parser));
        return this;
    }

    @Override
    public InterfaceReader textRenderer(final TextRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    public Component renderText(final Audience audience, final String text, final TagResolver... resolvers) {
        return renderer.renderText(text, audience, resolvers);
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
                .ifPresent(title -> builder.title(player -> renderText(player, title)));

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
                .map(actions -> (BiConsumer<InterfaceSession, Reason>) (session, reason) -> actions.accept(session))
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
                        .map(session -> (ClickAction) session::accept)
                        .map(clickAction::andThen)
                        .orElse(clickAction))
                .or(() -> parseActions(object)
                        .map(session -> session::accept));
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public Optional<Consumer<InterfaceSession>> parseActions(final JsonObject object) {
        final Function<RegisteredActionParser<?>, @Nullable Consumer<InterfaceSession>> function = p -> {
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
    public Optional<Predicate<InterfaceSession>> parseConditions(final JsonObject object) {
        final Function<RegisteredConditionParser<?>, @Nullable Predicate<InterfaceSession>> function = p -> {
            final var parser = (RegisteredConditionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return conditionParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(Predicate::and);
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    private Optional<Consumer<ItemStack>> parseItems(final JsonObject object) {
        final Function<RegisteredItemParser<?>, @Nullable Consumer<ItemStack>> function = p -> {
            final var parser = (RegisteredItemParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return itemParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen);
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    private Optional<BiConsumer<ItemStack, RenderContext>> parseDynamicItems(final JsonObject object) {
        final Function<RegisteredDynamicItemParser<?>, @Nullable BiConsumer<ItemStack, RenderContext>> function = p -> {
            final var parser = (RegisteredDynamicItemParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) return null;
            return parser.parser().parse(parser.type().cast(element), this);
        };
        return dynamicItemParsers.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(BiConsumer::andThen);
    }

    private <T extends JsonElement> Optional<T> get(final JsonObject object, final String key, final Class<T> type) {
        return Optional.ofNullable(object.get(key)).filter(type::isInstance).map(type::cast);
    }

    private Renderer readItemRenderer(final JsonObject object) {
        final var item = get(object, "item", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new IllegalStateException("Missing or invalid item"));
        final var itemStack = Bukkit.getItemFactory().createItemStack(item);
        parseItems(object).ifPresent(parser -> parser.accept(itemStack));
        return parseDynamicItems(object).<Renderer>map(consumer -> context -> {
            final var clone = itemStack.clone();
            consumer.accept(clone, context);
            return clone;
        }).orElseGet(() -> context -> itemStack.clone());
    }

    private Optional<ClickAction> readClickActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> parseClickActions(object).map(actions ->
                        parseConditions(object).<ClickAction>map(conditions -> context -> {
                            if (conditions.test(context)) actions.click(context);
                        }).orElse(actions)
                ).orElse(null))
                .filter(Objects::nonNull)
                .reduce(ClickAction::andThen);
    }

    private Optional<Consumer<InterfaceSession>> readActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> parseActions(object).map(actions ->
                        parseConditions(object).<Consumer<InterfaceSession>>map(conditions -> session -> {
                            if (conditions.test(session)) actions.accept(session);
                        }).orElse(actions)
                ).orElse(null))
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen);
    }
}
