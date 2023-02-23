package ru.mcsnapix.snapistones.modules.menu.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.api.events.block.ProtectedBlockInteractEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.enums.ClickAction;
import ru.mcsnapix.snapistones.modules.menu.inventory.PaginatedInventory;
import ru.mcsnapix.snapistones.modules.menu.menus.Menu;
import ru.mcsnapix.snapistones.utils.WGUtil;

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
        ProtectionBlock pb = event.getProtectionBlock();

        if (!WGUtil.hasPlayerInRegion(region, player)) {
            player.sendMessage(settings.get("Command.NoMember"));
            return;
        }

        new Menu(settings, player, region, pb).open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof PaginatedInventory) {
            PaginatedInventory paginatedMenu = (PaginatedInventory) inventory.getHolder();
            int slot = event.getRawSlot();

            if (slot == 45 && paginatedMenu.getCurrentPage() > 0) {
                paginatedMenu.updateInventory(true, false);
                event.setCancelled(true);
            } else if (slot == 53 && (paginatedMenu.getCurrentPage() + 1) * paginatedMenu.getMaxItemsPerPage() < paginatedMenu.getAllPlayers().size()) {
                paginatedMenu.updateInventory(false, true);
                event.setCancelled(true);
            } else if (slot >= 0 && slot < paginatedMenu.getMaxItemsPerPage()) {
                event.setCancelled(true);
            }
        }
    }

}
