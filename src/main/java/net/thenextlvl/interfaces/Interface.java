package net.thenextlvl.interfaces;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.thenextlvl.interfaces.reader.InterfaceReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an interface that can be opened by players.
 *
 * @since 0.1.0
 */
public sealed interface Interface permits SimpleInterface, PaginatedInterface {
    /**
     * Returns the type of menu used for this interface.
     *
     * @return the type of menu
     * @since 0.3.0
     */
    @Contract(pure = true)
    MenuType menuType();

    /**
     * Returns the layout of the interface.
     *
     * @return the layout of the interface
     * @since 0.1.0
     */
    @Contract(pure = true)
    Layout layout();

    /**
     * Returns the title of the interface for the player.
     *
     * @param player the player to get the title for
     * @return the title of the interface for the player
     * @since 0.1.0
     */
    @Contract(pure = true)
    @Nullable Component title(Player player);

    /**
     * Returns the action that is performed when the interface is opened.
     *
     * @return the action that is performed when the interface is opened
     * @since 0.2.0
     */
    @Contract(pure = true)
    @Nullable Consumer<InterfaceSession> onOpen();

    /**
     * Returns the action that is performed when the interface is closed.
     *
     * @return the action that is performed when the interface is closed
     * @since 0.2.0
     */
    @Contract(pure = true)
    @Nullable BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> onClose();

    /**
     * Returns the items that are displayed in the interface.
     *
     * @return the items that are displayed in the interface
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Map<Character, ActionItem> slots();

    /**
     * Opens the interface for the player, preserving the session state of an existing session.
     *
     * @param player  the player to open the interface for
     * @param session the session to use for the interface
     * @since 0.3.0
     */
    void open(Player player, InterfaceSession session);

    /**
     * Opens the interface for the player.
     *
     * @param player the player to open the interface for
     * @since 0.1.0
     */
    void open(Player player);

    /**
     * Creates a builder representing this interface.
     *
     * @since 0.1.0
     */
    @Contract(value = " -> new", pure = true)
    Builder toBuilder();

    /**
     * Creates a builder for an interface.
     *
     * @return a builder for an interface
     * @since 0.1.0
     */
    @Contract(value = " -> new", pure = true)
    static Builder builder() {
        return new SimpleInterface.Builder();
    }

    /**
     * A builder for fluently creating interfaces.
     *
     * @since 0.1.0
     */
    sealed interface Builder permits SimpleInterface.Builder {
        /**
         * Sets the number of rows for the interface.
         *
         * @param rows the number of rows
         * @return this builder
         * @throws IllegalArgumentException if the number of rows is invalid
         * @see #type(MenuType)
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder rows(@Range(from = 1, to = 6) int rows) throws IllegalArgumentException;

        /**
         * Sets the number of slots for the interface.
         *
         * @param slots the number of slots
         * @return this builder
         * @throws IllegalArgumentException if the number of slots is invalid
         * @see #type(MenuType)
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder slots(@MagicConstant(intValues = {5, 9, 18, 27, 36, 45, 54}) int slots) throws IllegalArgumentException;

        /**
         * Sets the type of menu for the interface.
         *
         * @param type the type of menu
         * @return this builder
         * @throws IllegalArgumentException if the inventory type is not creatable
         * @see #type(MenuType)
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder type(InventoryType type) throws IllegalArgumentException;

        /**
         * Sets the type of menu for the interface.
         *
         * @param type the type of menu
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder type(MenuType type);

        /**
         * Sets the title of the interface.
         *
         * @param title the title of the interface
         * @return this builder
         * @see #title(Function)
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder title(@Nullable Component title);

        /**
         * Sets the title of the interface for the player.
         *
         * @param title the title of the interface for the player
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder title(@Nullable Function<Player, Component> title);

        /**
         * Sets the layout of the interface.
         *
         * @param layout the layout of the interface
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder layout(Layout layout);

        /**
         * Sets an action item for a specific slot in the interface.
         *
         * @param slot       the character representing the slot
         * @param actionItem the action item for the slot
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_, _ -> this", pure = true)
        Builder slot(char slot, ActionItem actionItem);

        /**
         * Sets an item and action for a specific slot in the interface.
         *
         * @param slot   the character representing the slot
         * @param item   the item for the slot
         * @param action the action for the slot
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_, _, _ -> this", pure = true)
        Builder slot(char slot, ItemStack item, ClickAction action);

        /**
         * Sets a renderer and action for a specific slot in the interface.
         *
         * @param slot     the character representing the slot
         * @param renderer the renderer for the slot
         * @param action   the action for the slot
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_, _, _ -> this", pure = true)
        Builder slot(char slot, Renderer renderer, ClickAction action);

        /**
         * Sets an action that is performed when the interface is opened.
         *
         * @param handler the action that is performed when the interface is opened
         * @return this builder
         * @since 0.2.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder onOpen(@Nullable Consumer<InterfaceSession> handler);

        /**
         * Sets an action that is performed when the interface is closed.
         *
         * @param handler the action that is performed when the interface is closed
         * @return this builder
         * @since 0.2.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder onClose(@Nullable BiConsumer<InterfaceSession, InventoryCloseEvent.Reason> handler);

        /**
         * Builds the interface for the given plugin.
         *
         * @return the interface
         * @throws IllegalArgumentException if the interface is invalid
         * @since 0.4.0
         */
        @Contract(value = "_ -> new", pure = true)
        Interface build(JavaPlugin plugin) throws IllegalArgumentException;

        /**
         * Builds the interface for the plugin that provides this class.
         *
         * @return the interface
         * @throws IllegalArgumentException if the interface is invalid
         * @see JavaPlugin#getProvidingPlugin(Class)
         * @see #build(JavaPlugin)
         * @since 0.1.0
         */
        @Contract(value = " -> new", pure = true)
        Interface build() throws IllegalArgumentException;
    }

    // todo: remove
    static Interface example(final JavaPlugin plugin) {

        try {
            final var example = Interface.class.getResourceAsStream("example.json");
            Preconditions.checkState(example != null, "Missing example.json");
            final var read = InterfaceReader.reader()
                    .read(example);
            System.out.println(read);
        } catch (final IOException e) {
            e.printStackTrace(System.err);
        }

        return Interface.builder()
                .title(Component.text("Example"))
                .layout(Layout.builder()
                        .pattern("#-#-#-#-#",
                                "-       -",
                                "# abcba #",
                                "-       -",
                                "#-#-x-#-#")
                        .mask('a', context -> ItemStack.of(Material.IRON_INGOT, context.slot()))
                        .mask('b', context -> ItemStack.of(Material.GOLD_INGOT, context.index() + 1))
                        .mask('c', context -> ItemStack.of(Material.DIAMOND, context.row()))
                        .mask('#', context -> ItemStack.of(Material.BLACK_STAINED_GLASS_PANE, context.index() + 1))
                        .mask(' ', context -> ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE, context.column()))
                        .mask('-', ItemStack.of(Material.RED_STAINED_GLASS_PANE))
                        .build())
                .rows(5)
                .slot('x', ItemStack.of(Material.BARRIER), ClickAction.of(player -> {
                    System.out.println(player.getName() + " clicked the barrier");
                    player.closeInventory();
                }))
                .onOpen(context -> System.out.println(context.player().getName() + " opened the inventory"))
                .onClose((context, reason) -> System.out.println(context.player().getName() + " closed the inventory with reason " + reason))
                .build();
    }
}
