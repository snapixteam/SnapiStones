package ru.mcsnapix.snapistones.modules.menu.menus;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.erethon.headlib.HeadLib;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.menu.fastinv.FastInv;
import ru.mcsnapix.snapistones.modules.menu.fastinv.ItemBuilder;
import ru.mcsnapix.snapistones.xseries.XMaterial;

import java.util.List;
import java.util.UUID;

public class MainMenu extends FastInv {

    public MainMenu(Settings settings, Player player, ProtectedRegion region) {
        super(settings.getInt("Size"), settings.get("Title"));
        ConfigurationSection items = settings.getConfig().getConfigurationSection("Items");

        for (String item : items.getKeys(false)) {
            ConfigurationSection itemInfo = items.getConfigurationSection(item);

            String itemMaterial = itemInfo.getString("Material");
            int itemAmount = itemInfo.getInt("Amount");
            int itemSlot = itemInfo.getInt("Slot");
            String itemName = itemInfo.getString("Name");
            String itemHead = itemInfo.getString("Head");
            List<String> itemLore = itemInfo.getStringList("Lore");

            if (itemHead != null) {
//                ItemStack skull = HeadLib.ANIMAL_BIRD.toItemStack();

                ItemStack skull = HeadLib.setSkullOwner(
                        new ItemStack(Material.PLAYER_HEAD), UUID.randomUUID(), itemHead);
                setItem(itemSlot, new ItemBuilder(skull).amount(itemAmount).name(itemName).lore(itemLore).build());
                continue;
            }

            setItem(itemSlot, new ItemBuilder(XMaterial.matchXMaterial(itemMaterial).orElse(XMaterial.BEDROCK).parseMaterial())
                    .name(itemName).lore(itemLore).amount(itemAmount)
                    .build());
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        // do something
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // do something
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        // do something
    }
}
