package ru.mcsnapix.snapistones.api.events.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.mcsnapix.snapistones.api.ProtectionBlock;

@Getter
public class RegionCreateEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final ProtectedRegion region;
    private final ProtectionBlock protectionBlock;
    private final Location location;

    public RegionCreateEvent(Player player, ProtectedRegion region, ProtectionBlock protectionBlock, Location location) {
        this.player = player;
        this.region = region;
        this.protectionBlock = protectionBlock;
        this.location = location;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
