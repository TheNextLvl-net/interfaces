package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a paginated interface.
 *
 * @since 0.3.0
 */
public sealed interface PaginatedInterface<T> extends Interface permits SimplePaginatedInterface {
    /**
     * Gets the content key used for rendering the interface content.
     *
     * @return the content key
     * @since 0.3.0
     */
    @Contract(pure = true)
    char contentKey();

    /**
     * Transforms an entry into an action item.
     *
     * @param entry the entry to transform
     * @return the transformed action item
     * @since 0.3.0
     */
    @Contract(pure = true)
    ActionItem transformItem(T entry);

    /**
     * Gets the fallback action item.
     *
     * @return the fallback action item
     * @since 0.3.0
     */
    @Contract(pure = true)
    ActionItem fallback();

    /**
     * Builds a paginated interface with the specified configuration.
     *
     * @param template the interface builder template
     * @return the paginated interface builder
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> Builder<T> builder(final Interface.Builder template) {
        return new SimplePaginatedInterface.Builder<>(template);
    }

    /**
     * A builder for paginated interfaces.
     *
     * @param <T> the type of the entries
     * @since 0.3.0
     */
    sealed interface Builder<T> permits SimplePaginatedInterface.Builder {
        /**
         * Sets the mask character for the interface.
         *
         * @param key the mask character
         * @return this builder instance
         * @since 0.3.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> mask(char key);

        /**
         * Sets the content supplier for the interface.
         *
         * @param supplier the content supplier
         * @return this builder instance
         * @since 0.3.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> content(Supplier<? extends Collection<T>> supplier);

        /**
         * Sets the content collection for the interface.
         *
         * @param collection the content collection
         * @return this builder instance
         * @since 0.3.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> content(Collection<T> collection);

        /**
         * Sets the transformer function for the interface.
         *
         * @param function the transformer function
         * @return this builder instance
         * @since 0.3.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> transformer(Function<T, ActionItem> function);

        /**
         * Sets the fallback action item for the interface.
         *
         * @param fallback the fallback action item
         * @return this builder instance
         * @since 0.3.0
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder<T> fallback(ActionItem fallback);

        /**
         * Builds the paginated interface with the configured settings.
         *
         * @return the paginated interface
         * @since 0.3.0
         */
        @Contract(value = " -> new", pure = true)
        PaginatedInterface<T> build();
    }
}
