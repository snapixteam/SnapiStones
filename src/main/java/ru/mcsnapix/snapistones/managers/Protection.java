package ru.mcsnapix.snapistones.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.utils.WGUtil;
import ru.mcsnapix.snapistones.xseries.XMaterial;

import java.util.EnumMap;

public class Protection {
    @Getter
    private final EnumMap<XMaterial, ProtectionBlock> protectionBlockList = new EnumMap<>(XMaterial.class);

    public Protection() {
        SnapiStones plugin = SnapiStones.get();
        ConfigurationSection regionSection = plugin.getConfig().getConfigurationSection("Region");
        for (String i : regionSection.getKeys(false)) {
            XMaterial is = XMaterial.matchXMaterial(i).orElse(XMaterial.BEDROCK);

            ConfigurationSection radius = regionSection.getConfigurationSection(i + ".radius");
            protectionBlockList.put(is, ProtectionBlock.builder().item(is).symbol(regionSection.getString(i + ".char")).radiusX(radius.getInt("X")).radiusY(radius.getInt("Y")).radiusZ(radius.getInt("Z")).build());
        }

    }



    public boolean isProtectedBlock(XMaterial xMaterial) {
        return protectionBlockList.containsKey(xMaterial);
    }

    public String getRegionID(Location loc) {
        ApplicableRegionSet set = WorldGuard.getInstance()
                .getPlatform().getRegionContainer().createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(loc));
        String id = "";

        for (ProtectedRegion region : set) {
            id = region.getId();
        }

        return id;
    }

    public ProtectedRegion getRegion(Location loc) {
        ApplicableRegionSet set = WorldGuard.getInstance()
                .getPlatform().getRegionContainer().createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(loc));
        ProtectedRegion rg = WGUtil.getRegionManagerWithLocation(loc).getRegion(ProtectedRegion.GLOBAL_REGION);

        for (ProtectedRegion region : set) {
            rg = region;
        }

        return rg;
    }

    public boolean isRegionProtectionBlock(Location loc) {
        ApplicableRegionSet set = WorldGuard.getInstance()
                .getPlatform().getRegionContainer().createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(loc));
        boolean bool = false;

        for (ProtectedRegion region : set) {
            Location center = WGUtil.getCenter(region.getMinimumPoint(), region.getMaximumPoint());
            bool = center.toString().equals(loc.toString());
        }

        return bool;
    }

    public ProtectionBlock getProtectionBlock(XMaterial xMaterial) {
        return protectionBlockList.get(xMaterial);
    }
}
