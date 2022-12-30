package ru.mcsnapix.snapistones;

import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mcsnapix.snapistones.commands.RegionCommand;
import ru.mcsnapix.snapistones.handler.BlockHandler;
import ru.mcsnapix.snapistones.handler.ProtectionBlockHandler;
import ru.mcsnapix.snapistones.managers.Module;
import ru.mcsnapix.snapistones.managers.Protection;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;

@Getter
public final class SnapiStones extends JavaPlugin {
    private static SnapiStones snapiStones;
    private WorldGuardPlugin worldGuard;
    private Module moduleManager;
    private Protection protection;

    public static SnapiStones get() {
        return snapiStones;
    }

    @Override
    public void onEnable() {
        snapiStones = this;

        if (!(isPluginEnable("WorldGuard") && isPluginEnable("WorldEdit"))) {
            getLogger().info("§cWorldGuard или WorldEdit не работает! Плагин SnapiStones выключается");
            getServer().getPluginManager().disablePlugin(snapiStones);
            return;
        }
        worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");

        saveDefaultConfig();

        moduleManager = new Module();
        protection = new Protection();

        getServer().getPluginManager().registerEvents(new BlockHandler(snapiStones), snapiStones);
        getServer().getPluginManager().registerEvents(new ProtectionBlockHandler(snapiStones), snapiStones);

        PaperCommandManager manager = new PaperCommandManager(snapiStones);
        manager.registerCommand(new RegionCommand());
    }

    @Override
    public void onLoad() {
        if (isPluginEnable("CMI")) {
            return;
        }

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(FlagUtil.GREET_ACTION);
        registry.register(FlagUtil.FAREWELL_ACTION);
    }

    @Override
    public void onDisable() {

    }

    public boolean isPluginEnable(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }
}
