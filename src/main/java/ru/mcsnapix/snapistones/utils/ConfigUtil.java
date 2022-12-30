package ru.mcsnapix.snapistones.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;

@UtilityClass
public class ConfigUtil {
    private final SnapiStones plugin = SnapiStones.get();

    public String getString(String path) {
        return plugin.getConfig().getString(path);
    }

    public void sendMessage(Player player, String path) {
        player.sendMessage(getString(path));
    }

    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getString(path));
    }
}
