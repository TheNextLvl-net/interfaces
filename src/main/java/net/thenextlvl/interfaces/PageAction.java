package net.thenextlvl.interfaces;

import org.jetbrains.annotations.Contract;

// todo: move to a better place
//  we also need a simpler way to access paginated sessions
public final class PageAction {
    private PageAction() {
    }

    @Contract(value = " -> new", pure = true)
    public static ClickAction nextPage() {
        return context -> {
            final var session = InterfaceHandler.INSTANCE.getSession(context.player());
            if (session instanceof final PaginatedSession paginated) paginated.nextPage();
        };
    }

    @Contract(value = " -> new", pure = true)
    public static ClickAction previousPage() {
        return context -> {
            final var session = InterfaceHandler.INSTANCE.getSession(context.player());
            if (session instanceof final PaginatedSession paginated) paginated.previousPage();
        };
    }
}
