package ru.mcsnapix.snapistones.handler;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.api.SnapiStonesAPI;
import ru.mcsnapix.snapistones.api.events.region.RegionCreateEvent;
import ru.mcsnapix.snapistones.utils.ConfigUtil;
import ru.mcsnapix.snapistones.utils.WGUtil;
import ru.mcsnapix.snapistones.xseries.XMaterial;

public class BlockHandler implements Listener {
    private final SnapiStones plugin;

    public BlockHandler(SnapiStones plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        XMaterial xMaterial = XMaterial.matchXMaterial(block.getType());

        if (!SnapiStonesAPI.isProtectedBlock(xMaterial)) {
            return;
        }

        ProtectionBlock protectionBlock = plugin.getProtection().getProtectionBlock(xMaterial);

        if (!WorldGuardPlugin.inst().createProtectionQuery().testBlockPlace(player, block.getLocation(), block.getType())) {
            ConfigUtil.sendMessage(player, "language.cantProtectThat");
            event.setCancelled(true);
            return;
        }

        if (!createPSRegion(player, block.getLocation(), protectionBlock)) {
            event.setCancelled(true);
        }
    }

    public boolean createPSRegion(Player p, Location l, ProtectionBlock protectionBlock) {
        double bx = l.getX();
        double by = l.getY();
        double bz = l.getZ();

        RegionManager rm = WGUtil.getRegionManagerWithPlayer(p);

        String id = WGUtil.getNewRegionID(p, protectionBlock.getSymbol());

        BlockVector3 min = WGUtil.getMinVector(bx, by, bz, protectionBlock.getRadiusX(), protectionBlock.getRadiusY(), protectionBlock.getRadiusZ());
        BlockVector3 max = WGUtil.getMaxVector(bx, by, bz, protectionBlock.getRadiusX(), protectionBlock.getRadiusY(), protectionBlock.getRadiusZ());

        ProtectedRegion region = new ProtectedCuboidRegion(id, min, max);

        ApplicableRegionSet regions = rm.getApplicableRegions(region);

        if (regions.size() > 0) {
            ConfigUtil.sendMessage(p, "language.cantPutRegionInAnotherRegion");
            return false;
        }

        region.getOwners().addPlayer(p.getUniqueId());
        rm.addRegion(region);

        RegionCreateEvent regionCreateEvent = new RegionCreateEvent(p, region, protectionBlock, l);
        Bukkit.getPluginManager().callEvent(regionCreateEvent);

        ConfigUtil.sendMessage(p, "language.protected");

        return true;
    }
}
