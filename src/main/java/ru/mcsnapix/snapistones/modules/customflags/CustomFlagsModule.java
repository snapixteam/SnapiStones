package ru.mcsnapix.snapistones.modules.customflags;

import lombok.Getter;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.modules.customflags.listener.FlagListener;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;

@Getter
public class CustomFlagsModule implements IModule {
    private Settings settings;

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/customFlags", false);
        FlagUtil.register();
        main.getServer().getPluginManager().registerEvents(new FlagListener(settings), main);
        Bukkit.getLogger().info("§fМодуль §aCustomFlags §fзагружен");
    }
}
