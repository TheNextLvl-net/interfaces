package net.thenextlvl.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

sealed class SimpleStateHolder implements StateHolder permits SimpleInterface.Session {
    protected final Map<String, @Nullable Object> state;

    protected SimpleStateHolder(Map<String, @Nullable Object> state) {
        this.state = state;
    }

    @Override
    public <T> @Nullable T getState(String key, Class<T> type, @Nullable T fallback) {
        final var value = state.get(key);
        return type.isInstance(value) ? type.cast(value) : fallback;
    }

    @Override
    public <T> Optional<T> getState(String key, Class<T> type) {
        return Optional.ofNullable(state.get(key)).filter(type::isInstance).map(type::cast);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T setState(String key, @Nullable T value) {
        return (T) state.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T setStateIfAbsent(String key, @Nullable T value) {
        return (T) state.putIfAbsent(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T removeState(String key) {
        return (T) state.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeState(String key, BiFunction<String, @Nullable T, @Nullable T> remappingFunction) {
        return (T) state.compute(key, (s, o) -> remappingFunction.apply(s, (T) o));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeStateIfAbsent(String key, Function<String, @Nullable T> remappingFunction) {
        return (T) state.computeIfAbsent(key, remappingFunction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T computeStateIfPresent(String key, BiFunction<String, T, @Nullable T> remappingFunction) {
        return (T) state.computeIfPresent(key, (s, o) -> remappingFunction.apply(s, (T) o));
    }

    @Override
    public boolean hasState(String key) {
        return state.containsKey(key);
    }
}
