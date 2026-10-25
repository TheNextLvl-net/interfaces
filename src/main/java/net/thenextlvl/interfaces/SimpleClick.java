package net.thenextlvl.interfaces;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.OptionalInt;

final class SimpleClick {
    public static Click of(final InventoryClickEvent event) {
        return switch (event.getClick()) {
            case NUMBER_KEY -> {
                final var button = event.getHotbarButton();
                final var number = button != -1 ? OptionalInt.of(button + 1) : OptionalInt.empty();
                yield new Keyboard(event.getClick(), number);
            }
            case DROP, CONTROL_DROP, SWAP_OFFHAND -> new Keyboard(event.getClick(), OptionalInt.empty());
            default -> new Mouse(event.getClick());
        };
    }

    record Mouse(ClickType type) implements Click.Mouse {
        @Override
        public boolean left() {
            return type.isLeftClick() || type == ClickType.WINDOW_BORDER_LEFT;
        }

        @Override
        public boolean right() {
            return type.isRightClick() || type == ClickType.WINDOW_BORDER_RIGHT;
        }

        @Override
        public boolean middle() {
            return type == ClickType.MIDDLE;
        }

        @Override
        public boolean shift() {
            return type.isShiftClick();
        }

        @Override
        public boolean doubleClick() {
            return type == ClickType.DOUBLE_CLICK;
        }

        @Override
        public boolean outOfBounds() {
            return type == ClickType.WINDOW_BORDER_LEFT || type == ClickType.WINDOW_BORDER_RIGHT;
        }
    }

    record Keyboard(ClickType type, OptionalInt number) implements Click.Keyboard {
        @Override
        public boolean drop() {
            return dropOne() || dropAll();
        }

        @Override
        public boolean dropOne() {
            return type == ClickType.DROP;
        }

        @Override
        public boolean dropAll() {
            return type == ClickType.CONTROL_DROP;
        }

        @Override
        public boolean swap() {
            return type == ClickType.SWAP_OFFHAND;
        }
    }
}

