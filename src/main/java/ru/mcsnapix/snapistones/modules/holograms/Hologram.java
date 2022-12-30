package ru.mcsnapix.snapistones.modules.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.modules.holograms.listener.HologramListener;

@Getter
public class Hologram implements IModule {
    private Settings settings;

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/holograms", false);
        main.getServer().getPluginManager().registerEvents(new HologramListener(settings), main);
        Bukkit.getLogger().info("§fМодуль §aHologram §fзагружен");
    }

    @Override
    public void disable() {}
}
