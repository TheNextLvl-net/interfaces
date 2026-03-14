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

    @Contract(value = "_ -> new", pure = true)
    public static ClickAction changePage(int value) {
        return context -> {
            final var session = InterfaceHandler.INSTANCE.getSession(context.player());
            if (!(session instanceof final PaginatedSession paginated)) return;
            paginated.page(Math.clamp(paginated.page() + value, 0, paginated.pageCount() - 1));
        };
    }

    @Contract(value = "_ -> new", pure = true)
    public static ClickAction setPage(int value) {
        return context -> {
            final var session = InterfaceHandler.INSTANCE.getSession(context.player());
            if (!(session instanceof final PaginatedSession paginated)) return;
            paginated.page(Math.clamp(value, 0, paginated.pageCount() - 1));
        };
    }
}
