package ru.mcsnapix.snapistones.modules.regionsui;

import lombok.Getter;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;

@Getter
public class RegionSUI implements IModule {
    private Settings settings;

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/regionSUI", false);
        Bukkit.getLogger().info("§fМодуль §aRegionSUI §fзагружен");
    }
}