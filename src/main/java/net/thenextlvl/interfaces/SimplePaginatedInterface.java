package net.thenextlvl.interfaces;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

final class SimplePaginatedInterface<T> extends SimpleInterface implements PaginatedInterface<T> {
    private final char contentKey;
    private final Supplier<? extends Collection<? extends T>> contentSupplier;
    private final Function<T, ActionItem> itemFunction;
    private final ActionItem fallback;
    private final int[] contentSlots;

    SimplePaginatedInterface(
            final SimpleInterface template,
            final char contentKey,
            final Supplier<? extends Collection<? extends T>> contentSupplier,
            final Function<T, ActionItem> itemFunction,
            final ActionItem fallback
    ) {
        super(template.menuType(), template.title, template.layout(), template.onOpen(), template.onClose(), template.slots());
        this.contentKey = contentKey;
        this.contentSupplier = contentSupplier;
        this.itemFunction = itemFunction;
        this.fallback = fallback;
        this.contentSlots = resolveContentSlots(template.layout(), contentKey);
    }

    private static int[] resolveContentSlots(final Layout layout, final char contentKey) {
        final var pattern = layout.pattern();
        final var slots = new ArrayList<Integer>();
        var slot = 0;
        for (final var c : pattern.toCharArray()) {
            if (c == '\n') continue;
            if (c == contentKey) slots.add(slot);
            slot++;
        }
        return slots.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public char contentKey() {
        return contentKey;
    }

    @Override
    public ActionItem transformItem(final T entry) {
        return itemFunction.apply(entry);
    }

    @Override
    public ActionItem fallback() {
        return fallback;
    }

    @Override
    protected SimpleInterface.Session createSession(final Player player, final InventoryView view, final Map<String, @Nullable Object> state) {
        return new Session<>(player, view, this, state);
    }

    static final class Session<T> extends SimpleInterface.Session implements PaginatedSession {
        private List<T> content;
        private int page;

        private Session(
                final Player player,
                final InventoryView view,
                final SimplePaginatedInterface<T> paginatedInterface,
                final Map<String, @Nullable Object> state
        ) {
            super(player, view, paginatedInterface, state);
            this.page = 0;
            this.content = List.copyOf(paginatedInterface.contentSupplier.get());
        }

        @Override
        @SuppressWarnings("unchecked")
        public SimplePaginatedInterface<T> getInterface() {
            return (SimplePaginatedInterface<T>) super.getInterface();
        }

        @Override
        public int currentPage() {
            return page;
        }

        @Override
        public int pageSize() {
            return getInterface().contentSlots.length;
        }

        @Override
        public int pageCount() {
            return Math.max(1, (int) Math.ceil((double) content.size() / pageSize()));
        }

        @Override
        public boolean page(final int page) {
            if (page == this.page || page < 0 || page >= pageCount()) return false;
            this.page = page;
            refresh();
            return true;
        }

        @Override
        public void refresh() {
            updateContentItems();
            super.refresh();
        }

        @Override
        public void refresh(final char key) {
            if (key == getInterface().contentKey) updateContentItems();
            super.refresh(key);
        }

        private void updateContentItems() {
            this.content = List.copyOf(getInterface().contentSupplier.get());
            final var pages = pageCount();
            if (page >= pages) page = Math.max(0, pages - 1);

            final var offset = page * pageSize();
            final var paginated = getInterface();
            for (var i = 0; i < paginated.contentSlots.length; i++) {
                final var viewSlot = paginated.contentSlots[i];
                final var contentIndex = offset + i;
                final T element = contentIndex < content.size() ? content.get(contentIndex) : null;
                final var actionItem = element != null ? paginated.transformItem(element) : paginated.fallback();
                final var oldItem = paginated.items[viewSlot];
                paginated.items[viewSlot] = new SimpleInterface.Item(
                        paginated.contentKey, actionItem.renderer(), actionItem.action(),
                        i, oldItem.row(), oldItem.column(), viewSlot
                );
            }
        }
    }

    static final class Builder<T> implements PaginatedInterface.Builder<T> {
        private final SimpleInterface.Builder template;
        private @Nullable Character contentKey;
        private @Nullable Supplier<? extends Collection<T>> contentSupplier;
        private @Nullable Function<T, ActionItem> itemFunction;
        private ActionItem fallback = new ActionItem(context -> ItemStack.of(Material.AIR), context -> {
        });

        Builder(final Interface.Builder template) {
            this.template = (SimpleInterface.Builder) template;
        }

        @Override
        public PaginatedInterface.Builder<T> mask(final char key) {
            this.contentKey = key;
            return this;
        }

        @Override
        public PaginatedInterface.Builder<T> content(final Supplier<? extends Collection<T>> supplier) {
            this.contentSupplier = supplier;
            return this;
        }

        @Override
        public PaginatedInterface.Builder<T> content(final Collection<T> collection) {
            return content(() -> collection);
        }

        @Override
        public PaginatedInterface.Builder<T> transformer(final Function<T, ActionItem> function) {
            this.itemFunction = function;
            return this;
        }

        @Override
        public PaginatedInterface.Builder<T> fallback(final ActionItem fallback) {
            this.fallback = fallback;
            return this;
        }

        @Override
        public PaginatedInterface<T> build() {
            Preconditions.checkState(contentKey != null, "Content mask key not set");
            Preconditions.checkState(contentSupplier != null, "Content not set");
            Preconditions.checkState(itemFunction != null, "Content mapping function not set");
            template.slot(contentKey, fallback);
            return new SimplePaginatedInterface<>(template.build(), contentKey, contentSupplier, itemFunction, fallback);
        }
    }
}
