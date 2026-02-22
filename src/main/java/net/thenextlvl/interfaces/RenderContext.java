package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

public sealed interface RenderContext extends InterfaceSession permits ClickContext, SimpleRenderContext {
    @Contract(pure = true)
    int index();

    @Contract(pure = true)
    int row();

    @Contract(pure = true)
    int column();

    @Contract(pure = true)
    int slot();
}
