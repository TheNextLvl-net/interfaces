package net.thenextlvl.interfaces;

import java.util.Optional;

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
}
