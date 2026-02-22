package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;

public sealed interface InterfaceSession permits RenderContext, SimpleInterface.Session {
    Interface getInterface();

    @Contract(pure = true)
    Player getPlayer();

    @Contract(pure = true)
    InventoryView getView();

    @Contract(pure = true)
    <T> T getState(String key, Class<T> type, T fallback);

    void setState(String key, Object value);

    void refresh();

    void refresh(char key);

    void refreshSlot(int slot);
}
