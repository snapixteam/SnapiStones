package ru.mcsnapix.snapistones;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mcsnapix.snapistones.commands.RegionCommand;
import ru.mcsnapix.snapistones.handler.BlockHandler;
import ru.mcsnapix.snapistones.handler.ProtectionBlockHandler;
import ru.mcsnapix.snapistones.listeners.RegionListener;
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
    private BukkitAudiences adventure;
    private Chat chat;
    @Setter
    private RegionManager regionManager;
    @Setter private PaperCommandManager commandManager;

    public static SnapiStones get() {
        return snapiStones;
    }

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
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
        getServer().getPluginManager().registerEvents(new RegionListener(), snapiStones);

        commandManager = new PaperCommandManager(snapiStones);
        commandManager.registerCommand(new RegionCommand());
        registerCommandCompletions(commandManager);

        this.adventure = BukkitAudiences.create(this);
        setupChat();
    }

    @Override
    public void onLoad() {
        if (isPluginEnable("CMI")) {
            setRegionManager(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world"))));
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
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
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

        manager.getCommandCompletions().registerAsyncCompletion("myregionhomelistbymember", c -> {
            Player player = c.getPlayer();
            List<String> regionList = new ArrayList<>();

            for (Map.Entry<String, ProtectedRegion> entry : regionManager.getRegions().entrySet()) {
                ProtectedRegion region = entry.getValue();

                if (WGUtil.hasPlayerInRegion(region, player)) {
                    if (getModuleManager().getHome().hasLocationHome(region.getId())) {
                        regionList.add(region.getId());
                    }
                }
            }

            return ImmutableList.copyOf(regionList);
        });
    }

    public boolean isPluginEnable(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }
}
