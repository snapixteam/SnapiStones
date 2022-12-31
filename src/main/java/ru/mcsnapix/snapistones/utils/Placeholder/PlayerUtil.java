package ru.mcsnapix.snapistones.utils.placeholder;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PlayerUtil {
    public void sendArrayMessage(String path, Player player, ProtectedRegion region) {
        player.sendMessage(PlaceholderUtil.getStringArray(path, player, region));
    }

    public void sendStringMessage(String path, Player player, ProtectedRegion region) {
        player.sendMessage(PlaceholderUtil.getString(path, player, region));
    }
}
