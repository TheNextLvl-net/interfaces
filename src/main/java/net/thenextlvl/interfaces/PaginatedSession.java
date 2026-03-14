package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

/**
 * @since 0.3.0
 */
public sealed interface PaginatedSession extends InterfaceSession permits SimplePaginatedInterface.Session {
    PaginatedInterface<?> getInterface();

    @Contract(pure = true)
    int page();

    @Contract(pure = true)
    int pageSize();

    @Contract(pure = true)
    int pageCount();

    @Contract(pure = true)
    boolean hasNextPage();

    @Contract(pure = true)
    boolean hasPreviousPage();

    boolean nextPage();

    boolean previousPage();

    void page(@Range(from = 0, to = Integer.MAX_VALUE) int page) throws IndexOutOfBoundsException;
}
