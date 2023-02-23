package ru.mcsnapix.snapistones.utils.placeholder;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.mcsnapix.snapistones.api.ProtectionBlock;

@UtilityClass
public class PlayerUtil {
    public void sendArrayMessage(String path, Player player, ProtectedRegion region) {
        player.sendMessage(PlaceholderUtil.getStringArray(path, player, region, null));
    }

    public void sendArrayMessage(String path, Player player, ProtectedRegion region, ProtectionBlock pb) {
        player.sendMessage(PlaceholderUtil.getStringArray(path, player, region, pb));
    }

    public void sendStringMessage(String path, Player player, ProtectedRegion region) {
        player.sendMessage(PlaceholderUtil.getString(path, player, region, null));
    }

    public void sendDStringMessage(String string, Player player, ProtectedRegion region) {
        player.sendMessage(PlaceholderUtil.fillCommonPlaceholders(string, player, region, null));
    }

    public ItemStack getPlayerSkull(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        skull.setItemMeta(meta);
        return skull;
    }
}
