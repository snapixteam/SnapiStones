package ru.mcsnapix.snapistones.utils.placeholder;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.enums.Column;
import ru.mcsnapix.snapistones.modules.upgrade.managers.UpgradeDatabaseManager;
import ru.mcsnapix.snapistones.mysql.RegionDatabaseManager;
import ru.mcsnapix.snapistones.utils.WGUtil;
import ru.mcsnapix.snapistones.utils.serializer.LocationSerializer;

import javax.annotation.Nullable;
import java.util.*;

@UtilityClass
public class PlaceholderUtil {
    public @NonNull String fillCommonPlaceholders(
            @NonNull String value,
            @Nullable Player player,
            @Nullable ProtectedRegion region, @Nullable ProtectionBlock pb) {
        if (player != null) {
            value = value.replace("%player_name%", player.getName());
            value = value.replace("%vault_prefix%", SnapiStones.get().getChat().getPlayerPrefix(player));
        }
        if (region != null) {
            String id = region.getId();
            value = value.replace("%region_id%", id);
            value = value.replace("%region_owners%", getSetFormatString(region.getOwners().getUniqueIds()));
            value = value.replace("%region_members%", getSetFormatString(region.getMembers().getUniqueIds()));
            value = value.replace("%region_owners_size%", Integer.toString(region.getOwners().size()));
            value = value.replace("%region_members_size%", Integer.toString(region.getMembers().size()));
            value = value.replace("%region_creation_date%", RegionDatabaseManager.getCreationRegionFormattedDate(id));
            Location center = WGUtil.getCenter(region.getMinimumPoint(), region.getMaximumPoint());
            if (RegionDatabaseManager.hasRegionData(id)) {
                value = value.replace("%region_max_owners%", Integer.toString(UpgradeDatabaseManager.getMaxMembers(id, Column.MAX_OWNERS)));
                value = value.replace("%region_max_members%", Integer.toString(UpgradeDatabaseManager.getMaxMembers(id, Column.MAX_MEMBERS)));
            }
            value = value.replace("%region_center%", LocationSerializer.getFormattedLocation(center));
            value = value.replace("%region_has_home%", RegionDatabaseManager.hasLocation(id));
        }
        if (pb != null) {
            value = value.replace("%region_size%", pb.getFormattedRadius());
        }

        return value;
    }

    public List<String> getStringList(List<String> s, Player player, ProtectedRegion region, ProtectionBlock pb) {
        List<String> stringList = new ArrayList<>();

        for (String s1 : s) {
            switch (s1.toLowerCase()) {
                case "%region_members_list%":
                case "%region_owners_list%":
                    Column column = s1.equalsIgnoreCase("%region_members_list%") ? Column.MEMBERS : Column.OWNERS;
                    List<String> members = RegionDatabaseManager.getMembers(region.getId(), column);
                    System.out.println(members);
                    if (RegionDatabaseManager.hasMembers(region.getId(), column)) {
                        stringList.addAll(getFormattedListPlayer(members));
                    } else {
                        stringList.add("§f- §cНет игроков");
                    }
                    continue;
            }

            stringList.add(fillCommonPlaceholders(s1, player, region, pb));
        }

        return stringList;
    }

    public List<String> getFormattedListPlayer(List<String> players) {
        List<String> stringList = new ArrayList<>();

        for (String s2 : players) {
            stringList.add("§f- §a"+s2);
        }

        return stringList;
    }

    public List<String> getStringList(String path, Player player, ProtectedRegion region, ProtectionBlock pb) {
        return getStringList(SnapiStones.get().getConfig().getStringList(path), player, region, pb);
    }

    public String[] getStringArray(String path, Player player, ProtectedRegion region, ProtectionBlock pb) {
        return getStringList(path, player, region, pb).toArray(new String[0]);
    }

    public String[] getStringArray(List<String> s, Player player, ProtectedRegion region, ProtectionBlock pb) {
        return getStringList(s, player, region, pb).toArray(new String[0]);
    }

    public String getString(String path, Player player, ProtectedRegion region, ProtectionBlock pb) {
        String s = SnapiStones.get().getConfig().getString(path);
        return fillCommonPlaceholders(s, player, region, pb);
    }

    public String getSetFormatString(Set<UUID> uuids) {
        Set<String> set = new HashSet<>();
        for (UUID uuid : uuids) {
            set.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Iterator<String> iterator = set.iterator();

        StringBuilder stringBuilder = new StringBuilder();

        while (iterator.hasNext()) {
            String next = iterator.next();
            if (iterator.hasNext()) {
                stringBuilder.append("§f, §a");
            }
            stringBuilder.append(next);
        }

        if (stringBuilder.toString().equals("")) {
            return "Нет игроков";
        }

        return stringBuilder.toString();
    }
}
