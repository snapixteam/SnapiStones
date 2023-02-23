package ru.mcsnapix.snapistones.modules.upgrade.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.events.region.RegionEnterEvent;
import ru.mcsnapix.snapistones.api.events.region.RegionLeaveEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.upgrade.managers.UpgradeDatabaseManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class EffectListener implements Listener {
    private final Settings settings;
    private final Map<Player, ProtectedRegion> isOnABase = new WeakHashMap<>();
    private final SnapiStones plugin = SnapiStones.get();

    public EffectListener(Settings settings) {
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("mainWorld"))) return;
        checkEvents(e.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("mainWorld"))) return;
        checkEvents(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!e.getEntity().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("mainWorld"))) return;
        checkEvents(e.getEntity());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("mainWorld"))) return;
        checkEvents(e.getPlayer());
    }

    private void checkEvents(Player player) {
        if (player == null) return;

        Location loc = player.getLocation();
        ProtectedRegion region = plugin.getProtection().getRegion(loc);

        if (isOnABase.containsKey(player)) {
            if (region.getId().equals(ProtectedRegion.GLOBAL_REGION)) {
                Bukkit.getPluginManager().callEvent(new RegionLeaveEvent(player, isOnABase.get(player)));
                isOnABase.remove(player);
                return;
            }
            if (region != isOnABase.get(player)) {
                Bukkit.getPluginManager().callEvent(new RegionLeaveEvent(player, isOnABase.get(player)));
                Bukkit.getPluginManager().callEvent(new RegionEnterEvent(player, region));
                isOnABase.replace(player, region);
            }
        } else {
            if (!region.getId().equals(ProtectedRegion.GLOBAL_REGION)) {
                Bukkit.getPluginManager().callEvent(new RegionEnterEvent(player, region));
                isOnABase.put(player, region);
            }
        }
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();
        if (UpgradeDatabaseManager.hasActiveEffect(region.getId())) {
            List<String> idEffect = UpgradeDatabaseManager.getActiveEffects(region.getId());

            for (String effects : idEffect) {
                ConfigurationSection effectCS = settings.getConfig().getConfigurationSection("Effects."+effects);
                String effect = effectCS.getString("Effect");
                int level = effectCS.getInt("Level");

                player.addPotionEffect(PotionEffectType.getByName(effect).createEffect(Integer.MAX_VALUE, level));
            }
        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();
        for (PotionEffect pt : player.getActivePotionEffects()) {
            for (String s : UpgradeDatabaseManager.getActiveEffects(region.getId())) {
                if (s.equalsIgnoreCase(pt.getType().getName())) {
                    player.removePotionEffect(pt.getType());
                }
            }
        }
    }

    private String getPotionEffect(String idEffect) {
        ConfigurationSection effectCS = settings.getConfig().getConfigurationSection("Effects."+idEffect);
        String effect = effectCS.getString("Effect");
        return effect == null ? "" : effect;
    }
}
