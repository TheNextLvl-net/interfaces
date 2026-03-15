package net.thenextlvl.interfaces;

import org.bukkit.event.inventory.ClickType;

final class SimpleClickContext extends SimpleRenderContext implements ClickContext {
    private final ClickType clickType;

    public SimpleClickContext(
            final InterfaceSession session,
            final int index,
            final int row,
            final int column,
            final int slot,
            final ClickType clickType
    ) {
        super(session, index, row, column, slot);
        this.clickType = clickType;
    }

    @Override
    public ClickType clickType() {
        return clickType;
    }
}
