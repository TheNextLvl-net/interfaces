package net.thenextlvl.interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;

/**
 * Represents a renderer for an item.
 *
 * @since 0.1.0
 */
public interface Renderer {
    /**
     * Renders the item based on the given context.
     *
     * @param context the render context
     * @return the rendered item
     * @since 0.1.0
     */
    @CheckReturnValue
    ItemStack render(RenderContext context);
}
