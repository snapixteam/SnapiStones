package ru.mcsnapix.snapistones.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WGUtil {
    private final RegionManager regionManager = SnapiStones.get().getRegionManager();

    public BlockVector3 getMinVector(double bx, double by, double bz, long xRadius, long yRadius, long zRadius) {
        return BlockVector3.at(bx - xRadius, by - yRadius, bz - zRadius);
    }

    public BlockVector3 getMaxVector(double bx, double by, double bz, long xRadius, long yRadius, long zRadius) {
        return BlockVector3.at(bx + xRadius, by + yRadius, bz + zRadius);
    }

    public String getNewRegionID(Player player, String s) {
        String s2 = player.getName() + "_" + s + 1;

        for (int i = 1; i < 10; i++) {
            if (!hasRegion(player.getName() + "_" + s + i)) {
                s2 = player.getName() + "_" + s + i;
                break;
            }
        }

        return s2;
    }

    public Location getCenter(ProtectedRegion region) {
        return getCenter(region.getMinimumPoint(), region.getMaximumPoint());
    }

    public Location getCenter(BlockVector3 vMin, BlockVector3 vMax) {
        int cenX = (vMin.getX() + vMax.getX()) / 2;
        int cenY = (vMin.getY() + vMax.getY()) / 2;
        int cenZ = (vMin.getZ() + vMax.getZ()) / 2;

        return new Location(Bukkit.getWorld("world"), cenX, cenY, cenZ);
    }

    public boolean hasRegion(String id) {
        return getRegionManager().hasRegion(id);
    }

    public ProtectedRegion getRegion(String id) {
        return getRegionManager().getRegion(id);
    }

    public boolean hasPlayerInRegion(ProtectedRegion region, Player player) {
        return region.getOwners().contains(player.getUniqueId()) || region.getMembers().contains(player.getUniqueId());
    }

    public List<ProtectedRegion> getRegions(Player player) {
        List<ProtectedRegion> regions = new ArrayList<>();
        for (ProtectedRegion pr : getRegionManager().getRegions().values()) {
            if (hasPlayerInRegion(pr, player)) {
                regions.add(pr);
            }
        }
        return regions;
    }

    public static RegionManager getRegionManager() {
        return regionManager;
    }
}
