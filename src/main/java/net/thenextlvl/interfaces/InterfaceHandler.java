package net.thenextlvl.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class InterfaceHandler implements Listener {
    private static final Map<JavaPlugin, @Nullable InterfaceHandler> instances = new ConcurrentHashMap<>();
    private final Map<Player, SimpleInterface.Session> sessions = new ConcurrentHashMap<>();

    private InterfaceHandler() {
    }

    public static InterfaceHandler getInstance(final JavaPlugin plugin) {
        return instances.computeIfAbsent(plugin, ignored -> {
            final var instance = new InterfaceHandler();
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
            return instance;
        });
    }

    public void setSession(final Player player, final SimpleInterface.Session session) {
        sessions.put(player, session);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpen(final InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final var session = sessions.get(player);
        if (session == null || !event.getView().equals(session.view())) return;

        final var consumer = session.getInterface().onOpen();
        if (consumer != null) consumer.accept(session);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final var session = sessions.get(player);
        if (session == null || !event.getView().equals(session.view())) return;

        final var consumer = session.getInterface().onClose();
        if (consumer != null) consumer.accept(session, event.getReason());
        sessions.remove(player);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;
        final var session = sessions.get(player);
        if (session == null || !event.getView().equals(session.view())) return;

        session.handleClick(event);
        event.setCancelled(true);
    }
}
