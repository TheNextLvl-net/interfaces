package net.thenextlvl.interfaces.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.interfaces.ActionItem;
import net.thenextlvl.interfaces.ClickAction;
import net.thenextlvl.interfaces.Interface;
import net.thenextlvl.interfaces.InterfaceSession;
import net.thenextlvl.interfaces.Layout;
import net.thenextlvl.interfaces.PaginatedInterface;
import net.thenextlvl.interfaces.RenderContext;
import net.thenextlvl.interfaces.Renderer;
import net.thenextlvl.interfaces.reader.action.BroadcastActionParser;
import net.thenextlvl.interfaces.reader.action.CloseInterfaceActionParser;
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
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class SimpleInterfaceReader implements InterfaceReader, ParserContext {
    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(SimpleInterfaceReader.class);
    private static final Logger logger = plugin.getComponentLogger();

    private final Set<RegisteredClickActionParser<?>> clickActionParsers = new CopyOnWriteArraySet<>();
    private final Set<RegisteredActionParser<?>> actionParsers = new CopyOnWriteArraySet<>(Set.of(
            new RegisteredActionParser<>("send_message", JsonElement.class, MessageActionParser.INSTANCE),
            new RegisteredActionParser<>("broadcast", JsonElement.class, BroadcastActionParser.INSTANCE),
            new RegisteredActionParser<>("run_command", JsonPrimitive.class, CommandActionParser.INSTANCE),
            new RegisteredActionParser<>("run_console_command", JsonPrimitive.class, ConsoleCommandActionParser.INSTANCE),
            new RegisteredActionParser<>("play_sound", JsonObject.class, SoundActionParser.INSTANCE),
            new RegisteredActionParser<>("transfer", JsonPrimitive.class, TransferActionParser.INSTANCE),
            new RegisteredActionParser<>("connect", JsonPrimitive.class, ConnectActionParser.INSTANCE),
            new RegisteredActionParser<>("close_interface", JsonObject.class, CloseInterfaceActionParser.INSTANCE)
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
            new RegisteredDynamicItemParser<>("name", JsonElement.class, NameItemParser.INSTANCE),
            new RegisteredDynamicItemParser<>("lore", JsonArray.class, LoreItemParser.INSTANCE)
    ));
    private TextRenderer renderer = (text, audience, resolvers) -> {
        return MiniMessage.miniMessage().deserialize(text, resolvers);
    };

    @Override
    public Component renderText(final Audience audience, final JsonElement text, final TagResolver... resolvers) {
        try {
            return renderer.renderText(text, audience, resolvers);
        } catch (final ParserException e) {
            logger.warn("Failed to render text for player '{}': {}", audience.getOrDefault(Identity.NAME, "?"), e.getMessage());
            return Component.text(e.getMessage(), NamedTextColor.RED);
        }
    }

    @Override
    public Component renderText(final Audience audience, final String text, final TagResolver... resolvers) {
        try {
            return renderer.renderText(text, audience, resolvers);
        } catch (final ParserException e) {
            logger.warn("Failed to render text for player '{}': {}", audience.getOrDefault(Identity.NAME, "?"), e.getMessage());
            return Component.text(e.getMessage(), NamedTextColor.RED);
        }
    }

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
    public Interface.Builder read(final Path path) throws IOException {
        try (final var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    @Override
    public Interface.Builder read(final Reader reader) {
        return read(JsonParser.parseReader(reader).getAsJsonObject());
    }

    @Override
    public Interface.Builder read(final InputStream input) throws IOException {
        try (final var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    @Override
    public Interface.Builder read(final JsonObject object) throws IllegalStateException {
        final var pattern = get(object, "pattern", JsonArray.class).map(list ->
                list.asList().stream().map(JsonElement::getAsString).toArray(String[]::new)
        ).orElseThrow(() -> new IllegalStateException("Missing or invalid pattern"));

        final var builder = Interface.builder();

        Optional.ofNullable(object.get("title")).ifPresent(title -> {
            builder.title(player -> renderText(player, title));
        });

        final var layout = Layout.builder()
                .pattern(pattern);

        for (final var entry : object.entrySet()) {
            if (entry.getKey().length() != 1) continue;
            final var character = entry.getKey().charAt(0);
            if (!entry.getValue().isJsonObject()) logger.warn("Invalid entry '{}' for character '{}': {}",
                    entry.getKey(), character, entry.getValue());
            try {
                final var jsonObject = entry.getValue().getAsJsonObject();
                final var renderer = readItemRenderer(jsonObject);
                get(jsonObject, "click_actions", JsonArray.class)
                        .flatMap(this::readClickActions)
                        .ifPresentOrElse(actions -> {
                            builder.slot(character, new ActionItem(renderer, actions));
                        }, () -> {
                            layout.mask(character, renderer);
                        });
            } catch (final ParserException e) {
                logger.warn("Failed to parse item '{}': {}", entry.getKey(), e.getMessage());
                layout.mask(character, context -> ItemStack.of(Material.AIR));
            }
        }

        get(object, "on_open", JsonArray.class)
                .flatMap(this::readActions)
                .ifPresent(builder::onOpen);
        get(object, "on_close", JsonArray.class)
                .flatMap(this::readActions)
                .map(actions -> (BiConsumer<InterfaceSession, Reason>) (session, reason) -> actions.accept(session))
                .ifPresent(builder::onClose);

        return builder.layout(layout.build());
    }

    @Override
    public Interface.Builder readResource(final String path) throws IOException {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return read(Objects.requireNonNull(resource, "Missing resource: " + path));
        }
    }

    @Override
    public PaginatedInterface.Builder<?> readPaginated(final Path path) throws IOException {
        try (final var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return readPaginated(reader);
        }
    }

    @Override
    public PaginatedInterface.Builder<?> readPaginated(final Reader reader) {
        return readPaginated(JsonParser.parseReader(reader).getAsJsonObject());
    }

    @Override
    public PaginatedInterface.Builder<?> readPaginated(final InputStream input) throws IOException {
        try (final var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return readPaginated(reader);
        }
    }

    @Override
    public PaginatedInterface.Builder<?> readPaginated(final JsonObject object) throws IllegalStateException {
        final var contentMask = get(object, "content_mask", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .filter(s -> s.length() == 1)
                .map(s -> s.charAt(0))
                .orElseThrow(() -> new IllegalStateException("Missing or invalid 'content_mask' (expected a single character)"));

        final var builder = PaginatedInterface.builder(read(object))
                .mask(contentMask);

        get(object, String.valueOf(contentMask), JsonObject.class).ifPresent(fallbackObject -> {
            try {
                final var renderer = readItemRenderer(fallbackObject);
                final var action = get(fallbackObject, "click_actions", JsonArray.class)
                        .flatMap(this::readClickActions)
                        .orElse(context -> {
                        });
                builder.fallback(new ActionItem(renderer, action));
            } catch (final ParserException e) {
                logger.warn("Failed to parse fallback item for content mask '{}': {}", contentMask, e.getMessage());
            }
        });

        return builder;
    }

    @Override
    public PaginatedInterface.Builder<?> readPaginatedResource(final String path) throws IOException {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return readPaginated(Objects.requireNonNull(resource, "Missing resource: " + path));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ClickAction> parseClickActions(final JsonObject object) {
        final List<ClickAction> results = new ArrayList<>();
        for (final var p : clickActionParsers) {
            final var parser = (RegisteredClickActionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) {
                if (element != null) logger.warn("Click action '{}' expected {}, but got {}", parser.id(),
                        parser.type().getSimpleName(), element.getClass().getSimpleName());
                continue;
            }
            try {
                results.add(parser.parser().parse(parser.type().cast(element), this));
            } catch (final ParserException e) {
                logger.warn("Failed to parse click action '{}': {}", parser.id(), e.getMessage());
            } catch (final RuntimeException e) {
                logger.warn("Failed to parse click action '{}': {}", parser.id(), e.getMessage(), e);
            }
        }
        return results.stream()
                .reduce(ClickAction::andThen)
                .map(clickAction -> parseActions(object)
                        .map(session -> (ClickAction) session::accept)
                        .map(clickAction::andThen)
                        .orElse(clickAction))
                .or(() -> parseActions(object)
                        .map(session -> session::accept));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Consumer<InterfaceSession>> parseActions(final JsonObject object) {
        final List<Consumer<InterfaceSession>> results = new ArrayList<>();
        for (final var p : actionParsers) {
            final var parser = (RegisteredActionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) {
                if (element != null) logger.warn("Action '{}' expected {}, but got {}", parser.id(),
                        parser.type().getSimpleName(), element.getClass().getSimpleName());
                continue;
            }
            try {
                results.add(parser.parser().parse(parser.type().cast(element), this));
            } catch (final ParserException e) {
                logger.warn("Failed to parse action '{}': {}", parser.id(), e.getMessage());
            } catch (final RuntimeException e) {
                logger.warn("Failed to parse action '{}': {}", parser.id(), e.getMessage(), e);
            }
        }
        return results.stream().reduce(Consumer::andThen);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Predicate<InterfaceSession>> parseConditions(final JsonObject object) {
        final List<Predicate<InterfaceSession>> results = new ArrayList<>();
        for (final var p : conditionParsers) {
            final var parser = (RegisteredConditionParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) {
                if (element != null) logger.warn("Condition '{}' expected {}, but got {}", parser.id(),
                        parser.type().getSimpleName(), element.getClass().getSimpleName());
                continue;
            }
            try {
                results.add(parser.parser().parse(parser.type().cast(element), this));
            } catch (final ParserException e) {
                logger.warn("Failed to parse condition '{}': {}", parser.id(), e.getMessage());
            } catch (final RuntimeException e) {
                logger.warn("Failed to parse condition '{}': {}", parser.id(), e.getMessage(), e);
            }
        }
        return results.stream().reduce(Predicate::and);
    }

    @SuppressWarnings("unchecked")
    private Optional<Function<ItemStack, ItemStack>> parseItems(final JsonObject object) {
        final List<Function<ItemStack, ItemStack>> results = new ArrayList<>();
        for (final var p : itemParsers) {
            final var parser = (RegisteredItemParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) {
                if (element != null) logger.warn("Item property '{}' expected {}, but got {}", parser.id(),
                        parser.type().getSimpleName(), element.getClass().getSimpleName());
                continue;
            }
            try {
                results.add(parser.parser().parse(parser.type().cast(element), this));
            } catch (final ParserException e) {
                logger.warn("Failed to parse item property '{}': {}", parser.id(), e.getMessage());
            } catch (final RuntimeException e) {
                logger.warn("Failed to parse item property '{}': {}", parser.id(), e.getMessage(), e);
            }
        }
        return results.stream().reduce(Function::andThen);
    }

    @SuppressWarnings("unchecked")
    private Optional<BiFunction<ItemStack, RenderContext, ItemStack>> parseDynamicItems(final JsonObject object) {
        final List<BiFunction<ItemStack, RenderContext, ItemStack>> results = new ArrayList<>();
        for (final var p : dynamicItemParsers) {
            final var parser = (RegisteredDynamicItemParser<JsonElement>) p;
            final var element = object.get(parser.id());
            if (!parser.type().isInstance(element)) {
                if (element != null) logger.warn("Dynamic item property '{}' expected {}, but got {}", parser.id(),
                        parser.type().getSimpleName(), element.getClass().getSimpleName());
                continue;
            }
            try {
                results.add(parser.parser().parse(parser.type().cast(element), this));
            } catch (final ParserException e) {
                logger.warn("Failed to parse dynamic item property '{}': {}", parser.id(), e.getMessage());
            } catch (final RuntimeException e) {
                logger.warn("Failed to parse dynamic item property '{}': {}", parser.id(), e.getMessage(), e);
            }
        }
        return results.stream().reduce((first, second) -> (item, context) -> second.apply(first.apply(item, context), context));
    }

    private <T extends JsonElement> Optional<T> get(final JsonObject object, final String key, final Class<T> type) {
        return Optional.ofNullable(object.get(key)).filter(type::isInstance).map(type::cast);
    }

    private boolean isKnownItemKey(final String key) {
        if ("item".equals(key) || "click_actions".equals(key)) return true;
        return itemParsers.stream().anyMatch(p -> p.id().equals(key))
                || dynamicItemParsers.stream().anyMatch(p -> p.id().equals(key));
    }

    private boolean isKnownActionKey(final String key) {
        return actionParsers.stream().anyMatch(p -> p.id().equals(key))
                || clickActionParsers.stream().anyMatch(p -> p.id().equals(key))
                || conditionParsers.stream().anyMatch(p -> p.id().equals(key));
    }

    private Renderer readItemRenderer(final JsonObject object) throws ParserException {
        for (final var key : object.keySet()) {
            if (!isKnownItemKey(key)) logger.warn("Unknown item key '{}': no parser available", key);
        }
        final var item = get(object, "item", JsonPrimitive.class)
                .map(JsonPrimitive::getAsString)
                .orElseThrow(() -> new ParserException("Missing or invalid item"));

        final var created = Bukkit.getItemFactory().createItemStack(item);
        final var itemStack = parseItems(object)
                .map(parser -> parser.apply(created))
                .orElse(created);

        return parseDynamicItems(object).<Renderer>map(function -> context -> {
            final var clone = itemStack.clone();
            return function.apply(clone, context);
        }).orElseGet(() -> context -> itemStack.clone());
    }

    private Optional<ClickAction> readClickActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> {
                    for (final var key : object.keySet()) {
                        if (!isKnownActionKey(key)) logger.warn("Unknown click action '{}': no parser available", key);
                    }
                    return parseClickActions(object).map(actions ->
                            parseConditions(object).<ClickAction>map(conditions -> context -> {
                                if (conditions.test(context)) actions.click(context);
                            }).orElse(actions)
                    ).orElse(null);
                })
                .filter(Objects::nonNull)
                .reduce(ClickAction::andThen);
    }

    private Optional<Consumer<InterfaceSession>> readActions(final JsonArray array) {
        return array.asList().stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(object -> {
                    for (final var key : object.keySet()) {
                        if (!isKnownActionKey(key)) logger.warn("Unknown click action '{}': no parser available", key);
                    }
                    return parseActions(object).map(actions ->
                            parseConditions(object).<Consumer<InterfaceSession>>map(conditions -> session -> {
                                if (conditions.test(session)) actions.accept(session);
                            }).orElse(actions)
                    ).orElse(null);
                })
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen);
    }
}
