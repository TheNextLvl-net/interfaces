package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;

public sealed interface InterfaceSession permits RenderContext, SimpleInterface.Session {
    Interface getInterface();

    @Contract(pure = true)
    Player player();

    @Contract(pure = true)
    InventoryView view();

    @Contract(pure = true)
    <T> T state(String key, Class<T> type, T fallback);

    void state(String key, Object value);

    void refresh();

    void refresh(char key);

    void refreshSlot(int slot);
}
