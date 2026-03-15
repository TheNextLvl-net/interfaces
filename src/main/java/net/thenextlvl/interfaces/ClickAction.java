package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents an action that is performed when an item inside an interface is clicked.
 *
 * @since 0.1.0
 */
@FunctionalInterface
public interface ClickAction {
    /**
     * Called when an item inside an interface is clicked.
     *
     * @param context click context
     * @since 0.2.0
     */
    void click(ClickContext context);

    /**
     * Returns a composed {@code ClickAction} that performs, in sequence,
     * this action followed by the {@code other} action.
     *
     * @param other the action to perform after this action
     * @return a composed {@code ClickAction}
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    default ClickAction andThen(final ClickAction other) {
        return context -> {
            click(context);
            other.click(context);
        };
    }

    /**
     * Returns a {@code ClickAction} that performs the given action.
     *
     * @param action the action to perform
     * @return a {@code ClickAction} that performs the given action
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final BiConsumer<Player, ClickType> action) {
        return context -> action.accept(context.player(), context.clickType());
    }

    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final TriConsumer<Player, ClickType, Integer> action) {
        return context -> action.accept(context.player(), context.clickType(), context.slot());
    }

    /**
     * Returns a {@code ClickAction} that performs the given action.
     *
     * @param action the action to perform
     * @return a {@code ClickAction} that performs the given action
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final Consumer<Player> action) {
        return context -> action.accept(context.session().player());
    }

    /**
     * Returns a {@code ClickAction} that performs the given action.
     *
     * @param action the action to perform
     * @return a {@code ClickAction} that performs the given action
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final Runnable action) {
        return context -> action.run();
    }

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
