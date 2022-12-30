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
import ru.mcsnapix.snapistones.mysql.MySQL;

import java.sql.SQLException;

@Getter
public final class SnapiStones extends JavaPlugin {
    private static SnapiStones snapiStones;
    private WorldGuardPlugin worldGuard;
    private Module moduleManager;
    private Protection protection;
    private MySQL mySQL;

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

        try {
            mySQL = new MySQL(getConfig().getString("mysql.host", "127.0.0.1"), getConfig().getInt("mysql.port", 3306), getConfig().getString("mysql.database", "server_anarchy"), getConfig().getString("mysql.username", "root"), getConfig().getString("mysql.password", "root"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
        try {
            mySQL.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPluginEnable(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }
}
