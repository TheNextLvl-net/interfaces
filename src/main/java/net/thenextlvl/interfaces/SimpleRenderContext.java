package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;

record SimpleRenderContext(Player player, int index, int row, int column, int slot) implements RenderContext {
}