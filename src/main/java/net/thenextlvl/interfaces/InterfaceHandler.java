package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class InterfaceHandler implements Listener {
    public static final InterfaceHandler INSTANCE = new InterfaceHandler();

    private final Map<Player, SimpleInterface.Session> views = new HashMap<>();

    private InterfaceHandler() {
    }

    public SimpleInterface.@Nullable Session getSession(final Player player) {
        return views.get(player);
    }

    public void removeView(final Player player) {
        views.remove(player);
    }

    public void setView(final Player player, final SimpleInterface.Session session) {
        views.put(player, session);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpen(final InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final var session = getSession(player);
        if (session == null || !event.getView().equals(session.view())) return;

        final var consumer = session.getInterface().onOpen();
        if (consumer != null) consumer.accept(session);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final var session = getSession(player);
        if (session == null || !event.getView().equals(session.view())) return;

        final var consumer = session.getInterface().onClose();
        if (consumer != null) consumer.accept(session, event.getReason());
        removeView(player);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;
        final var session = getSession(player);
        if (session == null || !event.getView().equals(session.view())) return;

        session.getInterface().handleClick(session, event);
        event.setCancelled(true);
    }
}
