package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ClickAction {
    /**
     * Called when an item inside an interface is clicked.
     *
     * @param context click context
     */
    void click(ClickContext context);

    @Contract(value = "_ -> new", pure = true)
    default ClickAction andThen(final ClickAction other) {
        return context -> {
            click(context);
            other.click(context);
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final BiConsumer<Player, ClickType> action) {
        return context -> action.accept(context.getPlayer(), context.getClickType());
    }

    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final TriConsumer<Player, ClickType, Integer> action) {
        return context -> action.accept(context.getPlayer(), context.getClickType(), context.getSlot());
    }

    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final Consumer<Player> action) {
        return context -> action.accept(context.getPlayer());
    }

    @Contract(value = "_ -> new", pure = true)
    static ClickAction of(final Runnable action) {
        return context -> action.run();
    }

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
