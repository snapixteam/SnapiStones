package ru.mcsnapix.snapistones.api.events.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.mcsnapix.snapistones.api.ProtectionBlock;

public class RegionRemoveEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final ProtectedRegion region;
    private final ProtectionBlock protectionBlock;
    private final Location location;

    public RegionRemoveEvent(Player player, ProtectedRegion region, ProtectionBlock protectionBlock, Location location) {
        this.player = player;
        this.region = region;
        this.protectionBlock = protectionBlock;
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public Location getLocation() {
        return location;
    }

    public ProtectionBlock getProtectionBlock() {
        return protectionBlock;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
