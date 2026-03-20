package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents an object that holds state.
 *
 * @since 0.5.0
 */
public sealed interface StateHolder permits InterfaceSession, RenderContext, SimpleStateHolder {
    /**
     * Returns the value of the state with the given key, or the fallback value if no value is present.
     *
     * @param key      the key of the state
     * @param type     the type of the state
     * @param fallback the fallback value if no value is present
     * @param <T>      the type of the state
     * @return the value of the state with the given key, or the fallback value if no value is present
     * @since 0.5.0
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    <T> @Nullable T getState(String key, Class<T> type, @Nullable T fallback);

    /**
     * Returns the value of the state with the given key wrapped in an {@code Optional}.
     *
     * @param key  the key of the state
     * @param type the type of the state
     * @param <T>  the type of the state
     * @return the value of the state with the given key and type
     * @since 0.5.0
     */
    @Contract(pure = true)
    <T> Optional<T> getState(String key, Class<T> type);

    /**
     * Sets the value of the state with the given key to the given value.
     *
     * @param key   the key of the state
     * @param value the value of the state
     * @param <T>   the type of the state
     * @return the previous value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T setState(String key, @Nullable T value);

    /**
     * Sets the value of the state with the given key to the given value if the state is absent.
     *
     * @param key   the key of the state
     * @param value the value of the state
     * @param <T>   the type of the state
     * @return the previous value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T setStateIfAbsent(String key, @Nullable T value);

    /**
     * Removes the value of the state with the given key.
     *
     * @param key the key of the state
     * @param <T> the type of the state
     * @return the previous value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T removeState(String key);

    /**
     * Computes the value of the state with the given key using the given remapping function.
     *
     * @param key               the key of the state
     * @param remappingFunction the remapping function
     * @param <T>               the type of the state
     * @return the new value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeState(String key, BiFunction<String, @Nullable T, @Nullable T> remappingFunction);

    /**
     * Computes the value of the state with the given key using the given remapping function if the state is absent.
     *
     * @param key               the key of the state
     * @param remappingFunction the remapping function
     * @param <T>               the type of the state
     * @return the new value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeStateIfAbsent(String key, Function<String, @Nullable T> remappingFunction);

    /**
     * Computes the value of the state with the given key using the given remapping function.
     *
     * @param key               the key of the state
     * @param remappingFunction the remapping function
     * @param <T>               the type of the state
     * @return the new value of the state
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    <T> @Nullable T computeStateIfPresent(String key, BiFunction<String, T, @Nullable T> remappingFunction);

    /**
     * Returns whether the state with the given key is present.
     *
     * @param key the key of the state
     * @return whether the state with the given key is present
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean hasState(String key);
}
