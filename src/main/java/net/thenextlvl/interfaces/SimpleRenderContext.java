package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

non-sealed class SimpleRenderContext implements RenderContext {
    private final InterfaceSession session;
    private final int index;
    private final int row;
    private final int column;
    private final int slot;

    public SimpleRenderContext(
            final InterfaceSession session,
            final int index,
            final int row,
            final int column,
            final int slot
    ) {
        this.session = session;
        this.index = index;
        this.row = row;
        this.column = column;
        this.slot = slot;
    }

    @Override
    public InterfaceSession session() {
        return session;
    }

    @Override
    public Optional<PaginatedSession> paginatedSession() {
        return session instanceof final PaginatedSession paginated ? Optional.of(paginated) : Optional.empty();
    }

    @Override
    public Player player() {
        return session.player();
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int row() {
        return row;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public int slot() {
        return slot;
    }

    @Override
    public @Nullable <T> T getState(String key, Class<T> type, @Nullable T fallback) {
        return session.getState(key, type, fallback);
    }

    @Override
    public <T> Optional<T> getState(String key, Class<T> type) {
        return session.getState(key, type);
    }

    @Override
    public @Nullable <T> T setState(String key, @Nullable T value) {
        return session.setState(key, value);
    }

    @Override
    public @Nullable <T> T setStateIfAbsent(String key, @Nullable T value) {
        return session.setStateIfAbsent(key, value);
    }

    @Override
    public @Nullable <T> T removeState(String key) {
        return session.removeState(key);
    }

    @Override
    public @Nullable <T> T computeState(String key, BiFunction<String, @Nullable T, @Nullable T> remappingFunction) {
        return session.computeState(key, remappingFunction);
    }

    @Override
    public @Nullable <T> T computeStateIfAbsent(String key, Function<String, @Nullable T> remappingFunction) {
        return session.computeStateIfAbsent(key, remappingFunction);
    }

    @Override
    public @Nullable <T> T computeStateIfPresent(String key, BiFunction<String, T, @Nullable T> remappingFunction) {
        return session.computeStateIfPresent(key, remappingFunction);
    }

    @Override
    public boolean hasState(String key) {
        return session.hasState(key);
    }
}
