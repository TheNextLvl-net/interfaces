package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

public sealed interface RenderContext permits SimpleRenderContext {
    @Contract(pure = true)
    Player player();

    @Contract(pure = true)
    int index();

    @Contract(pure = true)
    int row();

    @Contract(pure = true)
    int column();

    @Contract(pure = true)
    int slot();
}
