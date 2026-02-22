package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

public sealed interface RenderContext extends InterfaceSession permits ClickContext, SimpleRenderContext {
    @Contract(pure = true)
    int getIndex();

    @Contract(pure = true)
    int getRow();

    @Contract(pure = true)
    int getColumn();

    @Contract(pure = true)
    int getSlot();
}
