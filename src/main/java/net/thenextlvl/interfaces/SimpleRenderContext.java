package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.Map;

non-sealed class SimpleRenderContext extends SimpleInterface.Session implements RenderContext {
    private final int index;
    private final int row;
    private final int column;
    private final int slot;

    public SimpleRenderContext(
            final Player player,
            final InventoryView view,
            final SimpleInterface interface_,
            final Map<String, Object> state,
            final int index,
            final int row,
            final int column,
            final int slot
    ) {
        super(player, view, interface_, state);
        this.index = index;
        this.row = row;
        this.column = column;
        this.slot = slot;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int row() {
        return row;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public int slot() {
        return slot;
    }
}
