package net.thenextlvl.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

sealed class SimpleStateHolder implements StateHolder permits SimpleInterface.Session {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(short.class, Short.class),
            Map.entry(char.class, Character.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(float.class, Float.class),
            Map.entry(double.class, Double.class)
    );
    protected final Map<String, @Nullable Object> state;

    protected SimpleStateHolder(final Map<String, @Nullable Object> state) {
        this.state = state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getState(final String key, final Class<T> type, @Nullable final T fallback) {
        final var value = state.get(key);
        if (value == null) return fallback;
        final var wrapped = PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);
        return wrapped.isAssignableFrom(value.getClass()) ? (T) value : fallback;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getState(final String key, final Class<T> type) {
        final var wrapped = PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);
        return Optional.ofNullable(state.get(key))
                .filter(value -> wrapped.isAssignableFrom(value.getClass()))
                .map(value -> (T) value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T setState(final String key, @Nullable final T value) {
        return (T) state.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T setStateIfAbsent(final String key, @Nullable final T value) {
        return (T) state.putIfAbsent(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T removeState(final String key) {
        return (T) state.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeState(final String key, final BiFunction<String, @Nullable T, @Nullable T> remappingFunction) {
        return (T) state.compute(key, (s, o) -> remappingFunction.apply(s, (T) o));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeStateIfAbsent(final String key, final Function<String, @Nullable T> remappingFunction) {
        return (T) state.computeIfAbsent(key, remappingFunction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeStateIfPresent(final String key, final BiFunction<String, T, @Nullable T> remappingFunction) {
        return (T) state.computeIfPresent(key, (s, o) -> remappingFunction.apply(s, (T) o));
    }

    @Override
    public boolean hasState(final String key) {
        return state.containsKey(key);
    }
}
