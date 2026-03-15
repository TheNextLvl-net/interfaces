package net.thenextlvl.interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Represents a layout for rendering items in a grid.
 *
 * @since 0.1.0
 */
sealed public interface Layout permits SimpleLayout {
    /**
     * Returns an unmodifiable map of masks and their corresponding renderers.
     *
     * @return an unmodifiable map of masks and renderers
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(value = " -> new", pure = true)
    Map<Character, Renderer> masks();

    /**
     * Returns the renderer associated with the given mask character.
     *
     * @param c the mask character
     * @return the renderer for the mask, or {@code null} if not found
     * @since 0.1.0
     */
    @Nullable
    @Contract(pure = true)
    Renderer renderer(char c);

    /**
     * Performs an action for each mask-renderer pair in the layout.
     *
     * @param action the action to perform
     * @since 0.1.0
     */
    void forEachMask(BiConsumer<Character, Renderer> action);

    /**
     * Returns whether the layout contains the given mask character.
     *
     * @param mask the mask character
     * @return {@code true} if the layout contains the mask, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean containsMask(char mask);

    /**
     * Returns whether the layout contains any mask characters.
     *
     * @return {@code true} if the layout contains masks, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean hasMasks();

    /**
     * Returns the pattern string representing the layout.
     *
     * @return the pattern string
     * @since 0.1.0
     */
    @Contract(pure = true)
    String pattern();

    /**
     * Returns the number of rows in the layout.
     *
     * @return the number of rows
     * @since 0.1.0
     */
    @Contract(pure = true)
    int rows();

    /**
     * Returns the number slots in the layout.
     *
     * @return the number of slots
     * @since 0.1.0
     */
    @Contract(pure = true)
    int size();

    /**
     * Creates a new layout builder with the same settings as this layout.
     *
     * @return a layout builder representing the same layout
     * @since 0.1.0
     */
    @Contract(value = " -> new", pure = true)
    Builder toBuilder();

    /**
     * Creates an empty layout.
     *
     * @return a new empty layout
     * @since 0.1.0
     */
    @Contract(value = " -> new", pure = true)
    static Layout empty() {
        return new SimpleLayout();
    }

    /**
     * Creates a layout from a pattern string.
     *
     * @param pattern the pattern string
     * @return a new layout from the pattern
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", pure = true)
    static Layout of(final String... pattern) {
        return new SimpleLayout(pattern);
    }

    /**
     * Creates a layout builder with the specified pattern.
     *
     * @param pattern the pattern string
     * @return a new layout builder
     * @since 0.4.0
     */
    @Contract(value = "_ -> new", pure = true)
    static Builder builder(final String... pattern) {
        return new SimpleLayout.Builder().pattern(pattern);
    }

    /**
     * A builder for creating layouts.
     *
     * @since 0.1.0
     */
    sealed interface Builder permits SimpleLayout.Builder {
        /**
         * Sets the pattern for the layout.
         *
         * @param pattern the pattern string
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_ -> this", pure = true)
        Builder pattern(String... pattern);

        /**
         * Sets a mask for the layout.
         *
         * @param mask the character to mask
         * @param item the item to use for masking
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_, _ -> this", pure = true)
        Builder mask(char mask, ItemStack item);

        /**
         * Sets a mask for the layout.
         *
         * @param mask        the character to mask
         * @param renderer the renderer to use for masking
         * @return this builder
         * @since 0.1.0
         */
        @Contract(value = "_, _ -> this", pure = true)
        Builder mask(char mask, Renderer renderer);

        /**
         * Builds the layout.
         *
         * @return the layout
         * @since 0.1.0
         */
        @Contract(value = " -> new", pure = true)
        Layout build();
    }
}
