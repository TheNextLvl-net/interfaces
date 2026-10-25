package net.thenextlvl.interfaces;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;

import java.util.OptionalInt;

/**
 * Represents a click performed by a player inside an interface.
 * <p>
 * A click is either a {@link Mouse mouse click} or a {@link Keyboard keyboard click}.
 *
 * @since 0.6.0
 */
public sealed interface Click permits Click.Keyboard, Click.Mouse {
    /**
     * Returns the underlying click type.
     *
     * @return the click type
     * @since 0.6.0
     */
    @Contract(pure = true)
    ClickType type();

    /**
     * Represents a click performed with the mouse.
     *
     * @since 0.6.0
     */
    sealed interface Mouse extends Click permits SimpleClick.Mouse {
        /**
         * Returns whether the left mouse button was used.
         * <p>
         * This includes shift-left clicks, double clicks and clicks on the left window border.
         *
         * @return {@code true} if this is a left click
         * @see ClickType#isLeftClick()
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean left();

        /**
         * Returns whether the right mouse button was used.
         * <p>
         * This includes shift-right clicks and clicks on the right window border.
         *
         * @return {@code true} if this is a right click
         * @see ClickType#isRightClick()
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean right();

        /**
         * Returns whether the middle mouse button was used.
         * <p>
         * Middle clicks are typically only sent by players in creative mode.
         *
         * @return {@code true} if this is a middle click
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean middle();

        /**
         * Returns whether the shift key was held while clicking.
         *
         * @return {@code true} if this is a shift click
         * @see ClickType#isShiftClick()
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean shift();

        /**
         * Returns whether the left mouse button was clicked twice in quick succession.
         *
         * @return {@code true} if this is a double click
         * @see ClickType#DOUBLE_CLICK
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean doubleClick();

        /**
         * Returns whether the click happened outside the inventory window.
         *
         * @return {@code true} if the window border was clicked
         * @see ClickType#WINDOW_BORDER_LEFT
         * @see ClickType#WINDOW_BORDER_RIGHT
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean outOfBounds();
    }

    /**
     * Represents a click performed with a keyboard key while hovering over a slot.
     *
     * @since 0.6.0
     */
    sealed interface Keyboard extends Click permits SimpleClick.Keyboard {
        /**
         * Returns the hotbar key that was pressed.
         *
         * @return the hotbar key ({@code 1}-{@code 9}),
         * or an empty optional if no hotbar key was pressed
         * @since 0.6.0
         */
        @Contract(pure = true)
        OptionalInt number();

        /**
         * Returns whether the drop key was pressed, regardless of the amount being dropped.
         *
         * @return {@code true} if this is a drop click
         * @see #dropOne()
         * @see #dropAll()
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean drop();

        /**
         * Returns whether the drop key was pressed to drop a single item.
         *
         * @return {@code true} if a single item is dropped
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean dropOne();

        /**
         * Returns whether the drop key was pressed while holding control to drop the entire stack.
         *
         * @return {@code true} if the entire stack is dropped
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean dropAll();

        /**
         * Returns whether the offhand swap key was pressed.
         *
         * @return {@code true} if the item is swapped with the offhand
         * @since 0.6.0
         */
        @Contract(pure = true)
        boolean swap();
    }
}
