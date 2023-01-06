package ru.mcsnapix.snapistones.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.mcsnapix.snapistones.utils.LocationSerializer;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("lobby")) {
            player.teleport(LocationSerializer.getDeserializedLocation("0.5;95.0;0.5;lobby;-90.0;0.0"));
        }
    }
}
