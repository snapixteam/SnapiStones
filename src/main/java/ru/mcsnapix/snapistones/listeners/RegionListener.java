package ru.mcsnapix.snapistones.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.region.RegionCreateEvent;
import ru.mcsnapix.snapistones.api.events.region.RegionRemoveEvent;
import ru.mcsnapix.snapistones.mysql.RegionDatabaseManager;

public class RegionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegionCreate(RegionCreateEvent event) {
        ProtectedRegion region = event.getRegion();
        Player player = event.getPlayer();
        RegionDatabaseManager.createRegion(region.getId(), player.getName());
        System.out.println();
    }
    // Название улучшения (Enum);уровень:Улучшения;уровень
    // String s;
    // s = UpgradeSerializer.serialize(String, Value);
    // mysql.update(s);
    // UpgradeSerializer.deserialize(s).getValue();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegionRemove(RegionRemoveEvent event) {
        ProtectedRegion region = event.getRegion();
        RegionDatabaseManager.removeRegion(region.getId());
    }
}
