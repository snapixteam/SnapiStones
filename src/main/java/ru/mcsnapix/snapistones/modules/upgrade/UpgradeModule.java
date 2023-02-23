package ru.mcsnapix.snapistones.modules.upgrade;

import lombok.Getter;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.modules.upgrade.listeners.EffectListener;

@Getter
public class UpgradeModule implements IModule {
    private Settings settings;

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/upgrade", false);
        main.getServer().getPluginManager().registerEvents(new EffectListener(settings), main);
        Bukkit.getLogger().info("§fМодуль §aUpgrade §fзагружен");
    }


}
