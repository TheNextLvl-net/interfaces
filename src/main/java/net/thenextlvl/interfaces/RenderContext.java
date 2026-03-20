package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Represents the context of a render event inside an interface.
 *
 * @since 0.1.0
 */
public sealed interface RenderContext extends StateHolder permits ClickContext, SimpleRenderContext {
    /**
     * Returns the session of the interface that triggered this event.
     *
     * @return the interface session
     * @since 0.4.0
     */
    @Contract(pure = true)
    InterfaceSession session();

    /**
     * Returns the paginated session of the interface that triggered this event if available.
     *
     * @return the paginated interface session
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<PaginatedSession> paginatedSession();

    /**
     * Returns the player corresponding to this context.
     *
     * @return the player
     * @since 0.5.0
     */
    @Contract(pure = true)
    Player player();

    /**
     * Returns the index of the item inside the interface.
     *
     * @return the item index
     * @since 0.1.0
     */
    @Contract(pure = true)
    int index();

    /**
     * Returns the row of the item inside the interface.
     *
     * @return the item row
     * @since 0.1.0
     */
    @Contract(pure = true)
    int row();

    /**
     * Returns the column of the item inside the interface.
     *
     * @return the item column
     * @since 0.1.0
     */
    @Contract(pure = true)
    int column();

    /**
     * Returns the slot of the item inside the interface.
     *
     * @return the item slot
     * @since 0.1.0
     */
    @Contract(pure = true)
    int slot();
}
