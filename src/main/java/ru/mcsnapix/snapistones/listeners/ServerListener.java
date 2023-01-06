package ru.mcsnapix.snapistones.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import ru.mcsnapix.snapistones.SnapiStones;

public class ServerListener implements Listener {
    private final SnapiStones plugin;

    public ServerListener(SnapiStones plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        plugin.setRegionManager(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world"))));
        plugin.registerCommandCompletions(plugin.getCommandManager());
    }
}
