package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

/**
 * Represents a session for a paginated interface.
 *
 * @since 0.3.0
 */
public sealed interface PaginatedSession extends InterfaceSession permits SimplePaginatedInterface.Session {
    /**
     * Returns the paginated interface that triggered this event.
     *
     * @return the paginated interface
     * @since 0.3.0
     */
    @Contract(pure = true)
    PaginatedInterface<?> getInterface();

    /**
     * Returns the current page of the paginated interface.
     *
     * @return the current page
     * @since 0.5.0
     */
    @Contract(pure = true)
    int getCurrentPage();

    /**
     * Returns the size of a page in the paginated interface.
     * <p>
     * This is the number of items that can be displayed on a single page.
     *
     * @return the page size
     * @since 0.5.0
     */
    @Contract(pure = true)
    int getPageSize();

    /**
     * Returns the total number of pages in the paginated interface.
     *
     * @return the total number of pages
     * @since 0.5.0
     */
    @Contract(pure = true)
    int getPageCount();

    /**
     * Moves to a specific page in the paginated interface.
     *
     * @param page the page number to move to
     * @return {@code true} if the page was changed, {@code false} otherwise
     * @since 0.5.0
     */
    @Contract(mutates = "this")
    boolean setPage(@Range(from = 0, to = Integer.MAX_VALUE) int page);
}
