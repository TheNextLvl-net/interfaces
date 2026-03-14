package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 0.3.0
 */
public sealed interface PaginatedInterface<T> extends Interface permits SimplePaginatedInterface {
    @Contract(pure = true)
    char contentKey();

    @Contract(pure = true)
    ActionItem transformItem(T entry);

    @Contract(pure = true)
    ActionItem fallback();

    @Contract(value = "_ -> new", pure = true)
    static <T> Builder<T> builder(Interface.Builder template) {
        return new SimplePaginatedInterface.Builder<>(template);
    }

    sealed interface Builder<T> permits SimplePaginatedInterface.Builder {
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> mask(char key);

        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> content(Supplier<? extends Collection<T>> supplier);

        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> content(Collection<T> collection);

        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> transformer(Function<T, ActionItem> function);

        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> fallback(ActionItem fallback);

        @Contract(value = " -> new", pure = true)
        PaginatedInterface<T> build();
    }
}
