package ru.mcsnapix.snapistones.api.events.block;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.enums.ClickAction;

@Builder
@Getter
public class ProtectedBlockInteractEvent extends Event {
    @Builder.Default
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;
    private ClickAction action;
    private ProtectedRegion region;
    private ProtectionBlock protectionBlock;
    private Location location;

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
