package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.Map;

final class SimpleClickContext extends SimpleRenderContext implements ClickContext {
    private final ClickType clickType;

    public SimpleClickContext(
            final Player player,
            final InventoryView view,
            final SimpleInterface interface_,
            final Map<String, Object> state,
            final int index,
            final int row,
            final int column,
            final int slot,
            final ClickType clickType
    ) {
        super(player, view, interface_, state, index, row, column, slot);
        this.clickType = clickType;
    }

    @Override
    public ClickType clickType() {
        return clickType;
    }
}
