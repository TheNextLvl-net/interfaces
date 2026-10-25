package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

/**
 * Represents the context of a click event inside an interface.
 *
 * @since 0.2.0
 */
public sealed interface ClickContext extends RenderContext permits SimpleClickContext {
    /**
     * Returns the click that triggered this event.
     *
     * @return the click
     * @since 0.6.0
     */
    @Contract(pure = true)
    Click click();
}
