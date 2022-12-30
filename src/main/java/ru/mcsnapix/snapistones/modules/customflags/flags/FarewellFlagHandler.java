package ru.mcsnapix.snapistones.modules.customflags.flags;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;

public class FarewellFlagHandler extends FlagValueChangeHandler<String> {
    public static final FarewellFlagHandler.Factory FACTORY = new FarewellFlagHandler.Factory();

    protected FarewellFlagHandler(Session session) {
        super(session, FlagUtil.FAREWELL_ACTION);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, String s) {

    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, String currentValue, String lastValue, MoveType moveType) {
        Player p = Bukkit.getPlayer(localPlayer.getUniqueId());

        for (ProtectedRegion r : toSet.getRegions()) {
            if (r.getFlag(FlagUtil.GREET_ACTION) != null) {
                return true;
            }
        }
        if (p != null && lastValue != null && !lastValue.equals(currentValue)) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', lastValue)));
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, String lastValue, MoveType moveType) {
        Player p = Bukkit.getPlayer(localPlayer.getUniqueId());
        if (p != null && lastValue != null) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', lastValue)));
        }
        return true;
    }

    public static class Factory extends Handler.Factory<FarewellFlagHandler> {
        @Override
        public FarewellFlagHandler create(Session session) {
            return new FarewellFlagHandler(session);
        }
    }
}
