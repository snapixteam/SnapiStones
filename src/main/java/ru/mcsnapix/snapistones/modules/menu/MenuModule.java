package ru.mcsnapix.snapistones.modules.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.modules.menu.fastinv.FastInvManager;
import ru.mcsnapix.snapistones.modules.menu.inventory.PaginatedInventory;
import ru.mcsnapix.snapistones.modules.menu.listener.ClickListener;
import ru.mcsnapix.snapistones.modules.menu.menus.Menu;

import java.util.ArrayList;

@Getter
public class MenuModule implements IModule {
    private Settings mainSettings;
    private Settings upgradesSettings;

    @Override
    public void loadModule(SnapiStones main) {
        FastInvManager.register(main);
        mainSettings = new Settings("modules/menu/mainMenu", false);
        upgradesSettings = new Settings("modules/menu/upgrades/mainMenu", false);
        main.getServer().getPluginManager().registerEvents(new ClickListener(mainSettings), main);
        Bukkit.getLogger().info("§fМодуль §aMenu §fзагружен");
    }
}