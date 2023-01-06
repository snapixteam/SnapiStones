package ru.mcsnapix.snapistones;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mcsnapix.snapistones.commands.RegionCommand;
import ru.mcsnapix.snapistones.handler.BlockHandler;
import ru.mcsnapix.snapistones.handler.ProtectionBlockHandler;
import ru.mcsnapix.snapistones.listeners.ServerListener;
import ru.mcsnapix.snapistones.managers.Module;
import ru.mcsnapix.snapistones.managers.Protection;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;
import ru.mcsnapix.snapistones.mysql.MySQL;
import ru.mcsnapix.snapistones.utils.WGUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public final class SnapiStones extends JavaPlugin {
    private static SnapiStones snapiStones;
    private WorldGuardPlugin worldGuard;
    private Module moduleManager;
    private Protection protection;
    private MySQL mySQL;
    @Setter
    private RegionManager regionManager;
    private PaperCommandManager commandManager;

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
        getServer().getPluginManager().registerEvents(new ServerListener(snapiStones), snapiStones);

        commandManager = new PaperCommandManager(snapiStones);
        commandManager.registerCommand(new RegionCommand());
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

    public void registerCommandCompletions(PaperCommandManager manager) {
        manager.getCommandCompletions().registerAsyncCompletion("myregionlistbyowner", c -> {
            Player player = c.getPlayer();

            List<String> regionList = new ArrayList<>();

            for (Map.Entry<String, ProtectedRegion> entry : regionManager.getRegions().entrySet()) {
                ProtectedRegion region = entry.getValue();

                if (region.getOwners().contains(player.getUniqueId())) {
                    regionList.add(region.getId());
                }
            }

            return ImmutableList.copyOf(regionList);
        });

        manager.getCommandCompletions().registerAsyncCompletion("regionlist", c -> {
            List<String> regionList = new ArrayList<>();

            for (Map.Entry<String, ProtectedRegion> entry : regionManager.getRegions().entrySet()) {
                ProtectedRegion region = entry.getValue();
                if (!region.getId().equalsIgnoreCase(ProtectedRegion.GLOBAL_REGION)) {
                    regionList.add(region.getId());
                }
            }

            return ImmutableList.copyOf(regionList);
        });

        manager.getCommandCompletions().registerAsyncCompletion("myregionlistbymember", c -> {
            Player player = c.getPlayer();
            List<String> regionList = new ArrayList<>();

            for (Map.Entry<String, ProtectedRegion> entry : regionManager.getRegions().entrySet()) {
                ProtectedRegion region = entry.getValue();

                if (WGUtil.hasPlayerInRegion(region, player)) {
                    regionList.add(region.getId());
                }
            }

            return ImmutableList.copyOf(regionList);
        });
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
