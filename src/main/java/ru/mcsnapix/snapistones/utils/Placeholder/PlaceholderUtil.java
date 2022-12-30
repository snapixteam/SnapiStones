package ru.mcsnapix.snapistones.utils.Placeholder;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class PlaceholderUtil {
    public @NonNull String fillCommonPlaceholders(
            @NonNull String value,
            @Nullable Player player,
            @Nullable ProtectedRegion region) {

        value = value.replace("%player_name%", player.getDisplayName());
        value = value.replace("%region_id%", region.getId());
        value = value.replace("%region_owners%", getFormatSetString(region.getOwners().getUniqueIds()));
        value = value.replace("%region_members%", getFormatSetString(region.getMembers().getUniqueIds()));
        // TODO: Доделать заполнители

        return value;
    }

    public List<String> getStringList(List<String> s, Player player, ProtectedRegion region) {
        List<String> stringList = new ArrayList<>();

        for (String s1 : s) {
            stringList.add(fillCommonPlaceholders(s1, player, region));
        }

        return stringList;
    }

    public List<String> getStringList(String path, Player player, ProtectedRegion region) {
        return getStringList(SnapiStones.get().getConfig().getStringList(path), player, region);
    }

    public String[] getStringArray(String path, Player player, ProtectedRegion region) {
        return getStringList(path, player, region).toArray(new String[0]);
    }

    public String[] getStringArray(List<String> s, Player player, ProtectedRegion region) {
        return getStringList(s, player, region).toArray(new String[0]);
    }

    public String getString(String path, Player player, ProtectedRegion region) {
        String s = SnapiStones.get().getConfig().getString(path);
        return fillCommonPlaceholders(s, player, region);
    }

    public String getFormatSetString(Set<UUID> uuids) {
        StringBuilder stringBuilder = new StringBuilder();

        for (UUID uuid : uuids) {
            stringBuilder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(" ");
        }

        if (stringBuilder.toString().equals("")) {
            return "Нет игроков";
        }

        return stringBuilder.toString();
    }
}
