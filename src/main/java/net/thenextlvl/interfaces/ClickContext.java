package net.thenextlvl.interfaces;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;

/**
 * Represents the context of a click event inside an interface.
 *
 * @since 0.2.0
 */
public sealed interface ClickContext extends RenderContext permits SimpleClickContext {
    /**
     * Returns the type of click that triggered this event.
     *
     * @return the type of click
     * @since 0.2.0
     */
    @Contract(pure = true)
    ClickType clickType();
}
