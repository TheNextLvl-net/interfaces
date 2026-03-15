package net.thenextlvl.interfaces;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
            this.content = List.copyOf(getInterface().contentSupplier.get());
            if (page >= pageCount()) page = Math.max(0, pageCount() - 1);

            for (var i = 0; i < getInterface().items.length; i++) {
                final var item = getInterface().items[i];
                if (item != null && item.key() == getInterface().contentKey) continue;
                refreshTemplateSlot(i);
            }
            refreshContentSlots();
        }

        @Override
        public void refresh(final char key) {
            if (key != getInterface().contentKey) super.refresh(key);
            this.content = List.copyOf(getInterface().contentSupplier.get());
            if (page >= pageCount()) page = Math.max(0, pageCount() - 1);
            refreshContentSlots();
        }

        @Override
        public void refreshSlot(final int slot) {
            Preconditions.checkElementIndex(slot, getInterface().items.length, "Slot");

            for (final var contentSlot : getInterface().contentSlots) {
                if (contentSlot == slot) {
                    refreshContentSlots();
                    return;
                }
            }
            refreshTemplateSlot(slot);
        }

        private void refreshContentSlots() {
            final var offset = page * pageSize();
            for (var i = 0; i < getInterface().contentSlots.length; i++) {
                final var viewSlot = getInterface().contentSlots[i];
                final var contentIndex = offset + i;
                final T element = contentIndex < content.size() ? content.get(contentIndex) : null;
                final var actionItem = element != null ? getInterface().transformItem(element) : getInterface().fallback();
                final var context = new SimpleRenderContext(this, i, 0, 0, viewSlot);
                view().setItem(viewSlot, actionItem.renderer().render(context));
            }
        }

        private void refreshTemplateSlot(final int slot) {
            final var item = getInterface().items[slot];
            if (item == null) {
                view().setItem(slot, null);
                return;
            }
            final var context = new SimpleRenderContext(this, item.index(), item.row(), item.column(), slot);
            view().setItem(slot, item.renderer().render(context));
        }

        @Override
        public void handleClick(final InventoryClickEvent event) {
            if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return;
            final var slot = event.getSlot();

            // todo: no custom logic, add the click actions to the item, remove entire method when done
            for (var i = 0; i < getInterface().contentSlots.length; i++) {
                if (getInterface().contentSlots[i] != slot) continue;
                final var contentIndex = page * pageSize() + i;
                final T element = contentIndex < content.size() ? content.get(contentIndex) : null;
                final var actionItem = element != null ? getInterface().transformItem(element) : getInterface().fallback();
                final var context = new SimpleClickContext(this, i, 0, 0, slot, event.getClick());
                actionItem.action().click(context);
                return;
            }

            if (slot < 0 || slot >= getInterface().items.length) return;
            final var item = getInterface().items[slot];
            if (item == null || item.action() == null) return;
            final var context = new SimpleClickContext(
                    this,
                    item.index(),
                    item.row(),
                    item.column(),
                    slot,
                    event.getClick()
            );
            item.action().click(context);
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
