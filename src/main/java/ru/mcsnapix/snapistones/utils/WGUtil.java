package ru.mcsnapix.snapistones.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@UtilityClass
public class WGUtil {

    public BlockVector3 getMinVector(double bx, double by, double bz, long xRadius, long yRadius, long zRadius) {
        return BlockVector3.at(bx - xRadius, by - yRadius, bz - zRadius);
    }

    public BlockVector3 getMaxVector(double bx, double by, double bz, long xRadius, long yRadius, long zRadius) {
        return BlockVector3.at(bx + xRadius, by + yRadius, bz + zRadius);
    }

    public String getNewRegionID(Player player, String s) {
        String s2 = player.getDisplayName() + "_" + s + 1;

        for (int i = 1; i < 10; i++) {
            if (!hasRegion(player, player.getDisplayName() + "_" + s + i)) {
                s2 = player.getDisplayName() + "_" + s + i;
                break;
            }
        }

        return s2;
    }

    public Location getCenter(BlockVector3 vMin, BlockVector3 vMax) {
        int cenX = (vMin.getX() + vMax.getX()) / 2;
        int cenY = (vMin.getY() + vMax.getY()) / 2;
        int cenZ = (vMin.getZ() + vMax.getZ()) / 2;

        return new Location(Bukkit.getWorld("world"), cenX, cenY, cenZ);
    }

    public boolean hasRegion(Player p, String id) {
        return getRegionManagerWithPlayer(p).hasRegion(id);
    }

    public ProtectedRegion getRegion(Player p, String id) {
        return getRegionManagerWithPlayer(p).getRegion(id);
    }

    public RegionManager getRegionManagerWithPlayer(Player p) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
    }

    public RegionManager getRegionManagerWithLocation(Location loc) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
    }

    public RegionManager getRegionManagerWithWorld(String world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
    }
}
