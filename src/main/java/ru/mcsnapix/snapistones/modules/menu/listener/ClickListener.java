package ru.mcsnapix.snapistones.modules.menu.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.block.ProtectedBlockInteractEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.enums.ClickAction;
import ru.mcsnapix.snapistones.modules.menu.menus.MainMenu;

@Getter
public class ClickListener implements Listener {
    private final Settings settings;

    public ClickListener(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onProtectedBlockInteract(ProtectedBlockInteractEvent event) {
        ClickAction action = event.getAction();

        if (!(action == ClickAction.LEFT_SHIFT || action == ClickAction.RIGHT_SHIFT)) {
            return;
        }

        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();

        new MainMenu(settings, player, region).open(player);
    }
}
