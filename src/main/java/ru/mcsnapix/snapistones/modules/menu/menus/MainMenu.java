package ru.mcsnapix.snapistones.modules.menu.menus;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.menu.fastinv.FastInv;
import ru.mcsnapix.snapistones.modules.menu.fastinv.ItemBuilder;
import ru.mcsnapix.snapistones.utils.Placeholder.PlaceholderUtil;

public class MainMenu extends FastInv {

    public MainMenu(Settings settings, Player player, ProtectedRegion region) {
        super(45, ChatColor.GOLD + "Example inventory");
        setItem(22,
                new ItemBuilder(Material.matchMaterial(settings.get("Information.material")))
                        .name("Информация")
                        .lore(PlaceholderUtil.getStringList(settings.getList("Information.lore"), player, region)).build(),
                e -> e.setCancelled(true)
        );
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
//        event.getPlayer().sendMessage(ChatColor.GOLD + "You opened the inventory");
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
//        event.getPlayer().sendMessage(ChatColor.GOLD + "You closed the inventory");
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        // do something
    }
}
