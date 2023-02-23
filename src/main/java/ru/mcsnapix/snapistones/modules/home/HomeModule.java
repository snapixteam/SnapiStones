package ru.mcsnapix.snapistones.modules.home;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.mysql.RegionDatabaseManager;
import ru.mcsnapix.snapistones.utils.placeholder.PlaceholderUtil;
import ru.mcsnapix.snapistones.utils.placeholder.PlayerUtil;

@Getter
public class HomeModule implements IModule {
    private Settings settings;

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/home", false);
        Bukkit.getLogger().info("§fМодуль §aHome §fзагружен");
    }

    private Location getLocationHome(String region) {
        if (RegionDatabaseManager.getLocation(region) == null) {
            return null;
        }
        return RegionDatabaseManager.getLocation(region);
    }

    public boolean hasLocationHome(String region) {
        return RegionDatabaseManager.getLocation(region) != null;
    }

    public void addHome(String region, Location location) {
        RegionDatabaseManager.setLocation(region, location);
    }

    public void teleportHome(Player player, ProtectedRegion region) {
        if (getLocationHome(region.getId()) != null) {
            player.teleport(getLocationHome(region.getId()));
            PlayerUtil.sendDStringMessage(settings.get("Home.Success"), player, region);
            return;
        }
        PlayerUtil.sendDStringMessage(settings.get("Home.NoHomeInRegion"), player, region);
    }
}