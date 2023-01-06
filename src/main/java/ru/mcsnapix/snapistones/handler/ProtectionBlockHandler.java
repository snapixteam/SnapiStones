package ru.mcsnapix.snapistones.handler;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.api.events.block.ProtectedBlockInteractEvent;
import ru.mcsnapix.snapistones.api.events.region.RegionRemoveEvent;
import ru.mcsnapix.snapistones.enums.ClickAction;
import ru.mcsnapix.snapistones.utils.ConfigUtil;
import ru.mcsnapix.snapistones.utils.WGUtil;
import ru.mcsnapix.snapistones.xseries.XMaterial;

public class ProtectionBlockHandler implements Listener {
    private final SnapiStones plugin;

    public ProtectionBlockHandler(SnapiStones plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Location location = block.getLocation();
        if (!plugin.getProtection().isRegionProtectionBlock(location)) {
            return;
        }

        XMaterial item = XMaterial.matchXMaterial(block.getType());
        Player player = event.getPlayer();
        ProtectionBlock protectionBlock = plugin.getProtection().getProtectionBlock(item);

        ProtectedRegion region = plugin.getProtection().getRegion(location);

        if (region.getOwners().contains(player.getUniqueId())) {
            ProtectedBlockInteractEvent protectedBlockInteractEvent =
                    ProtectedBlockInteractEvent.builder().player(player).action(getAction(player, action)).location(location)
                            .protectionBlock(protectionBlock).region(region).build();
            Bukkit.getPluginManager().callEvent(protectedBlockInteractEvent);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();

        if (!plugin.getProtection().isRegionProtectionBlock(location)) {
            return;
        }

        XMaterial item = XMaterial.matchXMaterial(block.getType());
        Player player = event.getPlayer();

        RegionManager regionManager = WGUtil.getRegionManagerWithLocation(location);
        ProtectionBlock protectionBlock = plugin.getProtection().getProtectionBlock(item);
        ProtectedRegion region = plugin.getProtection().getRegion(location);

        if (!region.getOwners().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        RegionRemoveEvent regionRemoveEvent = new RegionRemoveEvent(player, region, protectionBlock, location);
        Bukkit.getPluginManager().callEvent(regionRemoveEvent);

        regionManager.removeRegion(region.getId());
        event.setCancelled(true);
        block.setType(Material.AIR);
        block.getWorld().dropItem(block.getLocation(), protectionBlock.getItem().parseItem());

        ConfigurationSection section = SnapiStones.get().getConfig().getConfigurationSection("Region."+protectionBlock.getItem().name());
        location.getNearbyPlayers(6).forEach(p -> p.playSound(location, Sound.valueOf(section.getString("break-sound")), 1.0F, 1.0F));

        ConfigUtil.sendMessage(player, "language.inprotected");
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getProtection().isRegionProtectionBlock(block.getLocation()));
    }

    private ClickAction getAction(Player player, Action action) {
        if (player.isSneaking()) {
            if (action == Action.LEFT_CLICK_BLOCK) {
                return ClickAction.LEFT_SHIFT;
            } else {
                return ClickAction.RIGHT_SHIFT;
            }
        } else {
            if (action == Action.LEFT_CLICK_BLOCK) {
                return ClickAction.LEFT;
            } else {
                return ClickAction.RIGHT;
            }
        }
    }
}
