package net.thenextlvl.interfaces;

final class SimpleClickContext extends SimpleRenderContext implements ClickContext {
    private final Click click;

    public SimpleClickContext(
            final InterfaceSession session,
            final int index,
            final int row,
            final int column,
            final int slot,
            final Click click
    ) {
        super(session, index, row, column, slot);
        this.click = click;
    }

    @Override
    public Click click() {
        return click;
    }
}
