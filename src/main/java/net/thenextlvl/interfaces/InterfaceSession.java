package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public sealed interface InterfaceSession permits RenderContext, SimpleInterface.Session {
    Interface getInterface();

    @Contract(pure = true)
    Player player();

    @Contract(pure = true)
    InventoryView view();

    @Contract(pure = true)
    <T> T state(String key, Class<T> type, T fallback);

    /**
     * @since 0.3.0
     */
    @Contract(pure = true)
    <T> Optional<T> state(String key, Class<T> type);

    @Contract(mutates = "this")
    void state(String key, @Nullable Object value);

    /**
     * @since 0.3.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeState(String key, BiFunction<String, @Nullable T, @Nullable T> remappingFunction);

    /**
     * @since 0.3.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeStateIfAbsent(String key, Function<String, @Nullable T> remappingFunction);

    /**
     * @since 0.3.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeStateIfPresent(String key, BiFunction<String, @Nullable T, @Nullable T> remappingFunction);

    /**
     * @since 0.3.0
     */
    @Contract(pure = true)
    boolean hasState(String key);

    void refresh();

    void refresh(char key);

    void refreshSlot(int slot);
}
