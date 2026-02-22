package net.thenextlvl.interfaces;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;

public sealed interface ClickContext extends RenderContext permits SimpleClickContext {
    @Contract(pure = true)
    ClickType clickType();
}
