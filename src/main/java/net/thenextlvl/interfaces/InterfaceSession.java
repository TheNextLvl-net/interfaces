package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;

/**
 * Represents a session of an interface.
 *
 * @since 0.2.0
 */
public sealed interface InterfaceSession extends InventoryHolder, StateHolder permits SimpleInterface.Session, PaginatedSession {
    /**
     * Returns the interface corresponding to this session.
     *
     * @return the interface
     * @since 0.2.0
     */
    @Contract(pure = true)
    Interface getInterface();

    /**
     * Returns the player corresponding to this session.
     *
     * @return the player
     * @since 0.2.0
     */
    @Contract(pure = true)
    Player player();

    /**
     * Returns the inventory view corresponding to this session.
     *
     * @return the inventory view
     * @since 0.2.0
     */
    @Contract(pure = true)
    InventoryView view();

    /**
     * Refreshes the interface.
     *
     * @since 0.2.0
     */
    void refresh();

    /**
     * Refreshes the interface for the given key.
     *
     * @param key the key of the interface
     * @since 0.2.0
     */
    void refresh(char key);

    /**
     * Refreshes the interface for the given slot.
     *
     * @param slot the slot of the interface
     * @since 0.2.0
     */
    void refreshSlot(int slot) throws IndexOutOfBoundsException;
}
