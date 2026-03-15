package net.thenextlvl.interfaces;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

non-sealed class SimpleInterface implements Interface {
    private final @Nullable BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> onClose;
    private final @Nullable Consumer<InterfaceSession> onOpen;

    protected final @Nullable Function<Player, Component> title;
    protected final Item[] items;

    private final Layout layout;
    private final Map<Character, ActionItem> slots;
    private final MenuType type;

    protected SimpleInterface(
            final MenuType type,
            @Nullable final Function<Player, Component> title,
            final Layout layout,
            @Nullable final Consumer<InterfaceSession> onOpen,
            @Nullable final BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> onClose,
            final Map<Character, ActionItem> slots
    ) {
        this.type = type;
        this.title = title;
        this.layout = layout;
        this.onOpen = onOpen;
        this.onClose = onClose;
        this.slots = Map.copyOf(slots);

        var column = 1;
        var row = 1;
        var slot = 0;

        final var chars = layout.pattern().toCharArray();
        this.items = new Item[layout.size()];
        final var indices = new HashMap<Character, Integer>();

        for (final var c : chars) {
            if (c == '\n') {
                column = 1;
                row++;
                continue;
            }
            indices.compute(c, (k, v) -> v == null ? 0 : v + 1);

            final var actionItem = slots.get(c);
            final var item = actionItem != null
                    ? actionItem.renderer()
                    : layout.renderer(c);

            final var action = actionItem != null ? actionItem.action() : null;
            this.items[slot] = new Item(c, item, action, indices.get(c), row, column, slot);

            column++;
            slot++;
        }
    }

    @Override
    public MenuType menuType() {
        return type;
    }

    @Override
    public Layout layout() {
        return layout;
    }

    @Override
    public @Nullable Component title(final Player player) {
        return title == null ? null : title.apply(player);
    }

    @Override
    public @Nullable Consumer<InterfaceSession> onOpen() {
        return onOpen;
    }

    @Override
    public @Nullable BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> onClose() {
        return onClose;
    }

    @Override
    public Map<Character, ActionItem> slots() {
        return Map.copyOf(slots);
    }

    @Override
    public void open(final Player player, final InterfaceSession existing) {
        open(player, ((Session) existing).state);
    }

    @Override
    public void open(final Player player) {
        open(player, new ConcurrentHashMap<>());
    }

    private void open(final Player player, final Map<String, @Nullable Object> state) {
        final var view = type.create(player, title(player));
        final var session = createSession(player, view, state);
        session.refresh();
        InterfaceHandler.INSTANCE.setView(player, session);
        player.openInventory(view);
    }

    protected Session createSession(final Player player, final InventoryView view, final Map<String, @Nullable Object> state) {
        return new Session(player, view, this, state);
    }

    @Override
    public Interface.Builder toBuilder() {
        final var builder = new Builder();
        builder.slots.putAll(slots);
        return builder.type(type)
                .title(title)
                .layout(layout)
                .onOpen(onOpen)
                .onClose(onClose);
    }

    public void handleClick(final Session session, final InventoryClickEvent event) {
        if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return;
        final var slot = event.getSlot();
        if (slot < 0 || slot >= items.length) return;
        final var item = items[slot];
        if (item.action() == null) return;
        final var context = new SimpleClickContext(
                session,
                item.index(),
                item.row(),
                item.column(),
                slot,
                event.getClick()
        );
        item.action().click(context);
    }

    public record Item(
            char key,
            @Nullable Renderer renderer,
            @Nullable ClickAction action,
            int index,
            int row,
            int column,
            int slot
    ) {
    }

    public static sealed class Session implements InterfaceSession permits SimplePaginatedInterface.Session {
        protected final Map<String, @Nullable Object> state;
        private final SimpleInterface interface_;
        private final InventoryView view;
        private final Player player;

        Session(final Player player, final InventoryView view, final SimpleInterface interface_, final Map<String, @Nullable Object> state) {
            this.interface_ = interface_;
            this.player = player;
            this.view = view;
            this.state = state;
        }

        @Override
        @SuppressWarnings("ClassEscapesDefinedScope")
        public SimpleInterface getInterface() {
            return interface_;
        }

        @Override
        public final Player player() {
            return player;
        }

        @Override
        public final InventoryView view() {
            return view;
        }

        @Override
        public final <T> T state(final String key, final Class<T> type, final T fallback) {
            final var value = state.get(key);
            return type.isInstance(value) ? type.cast(value) : fallback;
        }

        @Override
        public final <T> Optional<T> state(final String key, final Class<T> type) {
            return Optional.ofNullable(state.get(key)).filter(type::isInstance).map(type::cast);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <T> @Nullable T state(final String key, final @Nullable T value) {
            return (T) state.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable <T> T stateIfAbsent(final String key, @Nullable final T value) {
            return (T) state.putIfAbsent(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <T> @Nullable T removeState(final String key) {
            return (T) state.remove(key);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final @Nullable <T> T computeState(final String key, final BiFunction<String, @Nullable T, @Nullable T> remappingFunction) {
            return (T) state.compute(key, (s, o) -> remappingFunction.apply(s, (T) o));
        }

        @Override
        @SuppressWarnings("unchecked")
        public final @Nullable <T> T computeStateIfAbsent(final String key, final Function<String, @Nullable T> remappingFunction) {
            return (T) state.computeIfAbsent(key, remappingFunction);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <T> @Nullable T computeStateIfPresent(final String key, final BiFunction<String, T, @Nullable T> remappingFunction) {
            return (T) state.computeIfPresent(key, (s, o) -> remappingFunction.apply(s, (T) o));
        }

        @Override
        public final boolean hasState(final String key) {
            return state.containsKey(key);
        }

        @Override
        public void refresh() {
            for (var i = 0; i < interface_.items.length; i++) refreshSlot(i);
        }

        @Override
        public void refresh(final char key) {
            for (var slot = 0; slot < interface_.items.length; slot++) {
                final var item = interface_.items[slot];
                if (item.key() != key) continue;
                refreshSlot(slot, item);
            }
        }

        @Override
        public void refreshSlot(final int slot) throws IndexOutOfBoundsException {
            Preconditions.checkElementIndex(slot, interface_.items.length, "Slot");
            refreshSlot(slot, interface_.items[slot]);
        }

        public void refreshSlot(final int slot, final @Nullable Item item) throws IndexOutOfBoundsException {
            final var context = item != null && item.renderer != null
                    ? new SimpleRenderContext(this, item.index(), item.row(), item.column(), slot)
                    : null;
            view.setItem(slot, context != null ? item.renderer().render(context) : null);
        }

        public void handleClick(final InventoryClickEvent event) {
            if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return;
            final var slot = event.getSlot();
            if (slot < 0 || slot >= interface_.items.length) return;
            final var item = interface_.items[slot];
            if (item.action() == null) return;
            final var context = new SimpleClickContext(
                    this,
                    item.index(),
                    item.row(),
                    item.column(),
                    slot,
                    event.getClick()
            );
            item.action().click(context);
        }

        @Override
        public Inventory getInventory() {
            return view.getTopInventory();
        }
    }

    public static final class Builder implements Interface.Builder {
        private @Nullable BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> onClose = null;
        private @Nullable Function<Player, Component> title = null;
        private @Nullable Consumer<InterfaceSession> onOpen = null;

        private Layout layout = Layout.empty();
        private @Nullable MenuType type = null;

        private final Map<Character, ActionItem> slots = new HashMap<>();

        @Override
        public Interface.Builder type(final InventoryType type) throws IllegalArgumentException {
            Preconditions.checkArgument(type.getMenuType() != null, "Inventory type %s is not creatable", type);
            return type(type.getMenuType());
        }

        @Override
        public Interface.Builder type(final MenuType type) {
            Preconditions.checkArgument(dimensions.containsKey(type), "Unsupported menu type: %s", type);
            this.type = type;
            return this;
        }

        @Override
        public Interface.Builder title(@Nullable final Component title) {
            return title(title == null ? null : player -> title);
        }

        @Override
        public Interface.Builder title(@Nullable final Function<Player, Component> title) {
            this.title = title;
            return this;
        }

        @Override
        @SuppressWarnings("MagicConstant")
        public Interface.Builder layout(final Layout layout) {
            this.layout = layout;
            if (type != null) return this;
            final var pattern = layout.pattern().split("\n");
            return slots(pattern.length == 1 && pattern[0].length() == 5 ? 5 : pattern.length * 9);
        }

        @Override
        public Interface.Builder slot(final char slot, final ActionItem actionItem) {
            this.slots.put(slot, actionItem);
            return this;
        }

        @Override
        public Interface.Builder slot(final char slot, final ItemStack item, final ClickAction action) {
            final var clone = item.clone();
            this.slots.put(slot, new ActionItem(context -> clone, action));
            return this;
        }

        @Override
        public Interface.Builder slot(final char slot, final Renderer renderer, final ClickAction action) {
            this.slots.put(slot, new ActionItem(renderer, action));
            return this;
        }

        @Override
        public Interface.Builder onOpen(@Nullable final Consumer<InterfaceSession> handler) {
            this.onOpen = handler;
            return this;
        }

        @Override
        public Interface.Builder onClose(@Nullable final BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> handler) {
            this.onClose = handler;
            return this;
        }

        @Override
        @SuppressWarnings("MagicConstant")
        public Interface.Builder rows(final int rows) throws IllegalArgumentException {
            return slots(rows * 9);
        }

        @Override
        public Interface.Builder slots(final int slots) throws IllegalArgumentException {
            this.type = switch (slots) {
                case 9 -> MenuType.GENERIC_9X1;
                case 18 -> MenuType.GENERIC_9X2;
                case 27 -> MenuType.GENERIC_9X3;
                case 36 -> MenuType.GENERIC_9X4;
                case 45 -> MenuType.GENERIC_9X5;
                case 54 -> MenuType.GENERIC_9X6;
                case 5 -> MenuType.HOPPER;
                default -> throw new IllegalArgumentException("Invalid number of slots: " + slots);
            };
            return this;
        }

        @Override
        @SuppressWarnings("ClassEscapesDefinedScope")
        public SimpleInterface build() throws IllegalArgumentException {
            Preconditions.checkArgument(type != null, "Menu type not set");
            final var dimensions = this.dimensions.get(type);
            Preconditions.checkArgument(dimensions != null, "Unsupported menu type: %s", type);
            if (!layout.pattern().isEmpty() || layout.hasMasks() || !slots.isEmpty()) {
                validatePattern(dimensions.getKey(), dimensions.getValue());
            }
            return new SimpleInterface(type, title, layout, onOpen, onClose, slots);
        }

        private final Map<MenuType, Map.Entry<Integer, Integer>> dimensions = Map.ofEntries(
                Map.entry(MenuType.GENERIC_9X1, Map.entry(1, 9)),
                Map.entry(MenuType.GENERIC_9X2, Map.entry(2, 9)),
                Map.entry(MenuType.GENERIC_9X3, Map.entry(3, 9)),
                Map.entry(MenuType.GENERIC_9X4, Map.entry(4, 9)),
                Map.entry(MenuType.GENERIC_9X5, Map.entry(5, 9)),
                Map.entry(MenuType.GENERIC_9X6, Map.entry(6, 9)),
                Map.entry(MenuType.GENERIC_3X3, Map.entry(3, 3)),
                Map.entry(MenuType.CRAFTER_3X3, Map.entry(3, 3)),
                Map.entry(MenuType.ANVIL, Map.entry(1, 3)),
                Map.entry(MenuType.BEACON, Map.entry(1, 1)),
                Map.entry(MenuType.BLAST_FURNACE, Map.entry(1, 1)),
                Map.entry(MenuType.BREWING_STAND, Map.entry(1, 1)),
                Map.entry(MenuType.CRAFTING, Map.entry(3, 3)),
                Map.entry(MenuType.ENCHANTMENT, Map.entry(1, 2)),
                Map.entry(MenuType.FURNACE, Map.entry(3, 1)),
                Map.entry(MenuType.GRINDSTONE, Map.entry(3, 1)),
                Map.entry(MenuType.HOPPER, Map.entry(1, 5)),
                Map.entry(MenuType.SHULKER_BOX, Map.entry(3, 9)),
                Map.entry(MenuType.SMITHING, Map.entry(1, 4)),
                Map.entry(MenuType.SMOKER, Map.entry(3, 1)),
                Map.entry(MenuType.CARTOGRAPHY_TABLE, Map.entry(3, 1)),
                Map.entry(MenuType.STONECUTTER, Map.entry(1, 2))
        );

        private void validatePattern(final int rows, final int cols) throws IllegalArgumentException {
            final var pattern = layout.pattern().split("\n");
            // validate that the pattern has the correct number of rows
            Preconditions.checkArgument(pattern.length == rows, "Invalid number of rows in pattern: found %s but expected %s", pattern.length, rows);
            for (var i = 0; i < pattern.length; i++) {
                // validate that each row has the correct number of columns
                Preconditions.checkArgument(pattern[i].length() == cols, "Invalid number of columns in row %s of pattern: found %s but expected %s", i, pattern[i].length(), cols);
            }

            // create a set of all characters in the pattern (excluding newlines)
            final var patternChars = layout.pattern().chars()
                    .mapToObj(c -> (char) c)
                    .filter(c -> c != '\n')
                    .collect(Collectors.toSet());

            // check that all characters in the pattern have a corresponding mask or slot
            patternChars.forEach(c -> {
                Preconditions.checkArgument(c == ' ' || layout.containsMask(c) || slots.containsKey(c), "Character '%s' in pattern has no corresponding mask or slot", c);
            });

            // create a set of all masks and slots
            final var maskChars = new HashSet<>(layout.masks().keySet());
            maskChars.addAll(slots.keySet());

            // check that all masks and slots are defined in the pattern
            maskChars.forEach(c -> {
                Preconditions.checkArgument(patternChars.contains(c), "Mask or slot '%s' is not defined in pattern", c);
            });
        }
    }

    static {
        final var plugin = JavaPlugin.getProvidingPlugin(SimpleInterface.class);
        plugin.getServer().getPluginManager().registerEvents(InterfaceHandler.INSTANCE, plugin);
    }
}
