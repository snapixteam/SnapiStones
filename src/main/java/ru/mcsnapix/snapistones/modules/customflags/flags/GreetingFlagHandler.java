package ru.mcsnapix.snapistones.modules.customflags.flags;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;

public class GreetingFlagHandler extends FlagValueChangeHandler<String> {

    public static final Factory FACTORY = new Factory();

    public GreetingFlagHandler(Session session) {
        super(session, FlagUtil.GREET_ACTION);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, String s) {

    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, String currentValue, String lastValue, MoveType moveType) {
        if (currentValue != null && !currentValue.equals(lastValue) && Bukkit.getPlayer(localPlayer.getUniqueId()) != null) {
            Bukkit.getPlayer(localPlayer.getUniqueId()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', currentValue)));
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, String lastValue, MoveType moveType) {
        return true;
    }

    public static class Factory extends Handler.Factory<GreetingFlagHandler> {
        @Override
        public GreetingFlagHandler create(Session session) {
            return new GreetingFlagHandler(session);
        }
    }
}
