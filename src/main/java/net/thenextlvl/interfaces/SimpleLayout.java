package net.thenextlvl.interfaces;


import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

final class SimpleLayout implements Layout {
    private final String pattern;
    private final Map<Character, Renderer> items;
    private final int rows;
    private final int size;

    private SimpleLayout(final String pattern, final Map<Character, Renderer> items) {
        this.pattern = pattern;
        this.items = Map.copyOf(items);
        this.rows = readRows(pattern);
        this.size = readSize(pattern);
    }

    public SimpleLayout(final String... pattern) {
        this.pattern = String.join("\n", pattern);
        this.items = Map.of();
        this.rows = readRows(this.pattern);
        this.size = readSize(this.pattern);
    }

    public SimpleLayout() {
        this.pattern = "";
        this.items = Map.of();
        this.rows = 0;
        this.size = 0;
    }

    private int readRows(final String pattern) {
        return (int) pattern.chars().filter(c -> c == '\n').count() + 1;
    }

    private int readSize(final String pattern) {
        return (int) pattern.chars().filter(c -> c != '\n').count();
    }

    @Override
    public Map<Character, Renderer> masks() {
        return Map.copyOf(items);
    }

    @Override
    public @Nullable Renderer renderer(final char c) {
        return items.get(c);
    }

    @Override
    public void forEachMask(final BiConsumer<Character, Renderer> action) {
        items.forEach(action);
    }

    @Override
    public boolean containsMask(final char mask) {
        return items.containsKey(mask);
    }

    @Override
    public boolean hasMasks() {
        return !items.isEmpty();
    }

    @Override
    public String pattern() {
        return pattern;
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Layout.Builder toBuilder() {
        final var builder = new Builder();
        builder.pattern = pattern;
        items.forEach(builder::mask);
        return builder;
    }

    static final class Builder implements Layout.Builder {
        private String pattern = "";
        private final Map<Character, Renderer> items = new HashMap<>();

        @Override
        public Layout.Builder pattern(final String... pattern) {
            this.pattern = String.join("\n", pattern);
            return this;
        }

        @Override
        public Layout.Builder mask(final char mask, final ItemStack item) {
            this.items.put(mask, context -> item);
            return this;
        }

        @Override
        public Layout.Builder mask(final char c, final Renderer renderer) {
            this.items.put(c, renderer);
            return this;
        }

        @Override
        public Layout build() {
            return new SimpleLayout(pattern, items);
        }
    }
}
