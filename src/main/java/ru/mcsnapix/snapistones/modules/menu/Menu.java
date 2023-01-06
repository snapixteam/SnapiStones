package ru.mcsnapix.snapistones.modules.menu;

import lombok.Getter;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.modules.menu.fastinv.FastInvManager;
import ru.mcsnapix.snapistones.modules.menu.listener.ClickListener;

@Getter
public class Menu implements IModule {
    private Settings mainSettings;

    @Override
    public void loadModule(SnapiStones main) {
        FastInvManager.register(main);
        mainSettings = new Settings("modules/menu/mainMenu", false);
        main.getServer().getPluginManager().registerEvents(new ClickListener(mainSettings), main);
        Bukkit.getLogger().info("§fМодуль §aMenu §fзагружен");
    }
}