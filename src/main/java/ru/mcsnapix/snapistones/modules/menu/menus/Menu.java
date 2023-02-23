package ru.mcsnapix.snapistones.modules.menu.menus;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.erethon.headlib.HeadLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.menu.MenuModule;
import ru.mcsnapix.snapistones.modules.menu.fastinv.FastInv;
import ru.mcsnapix.snapistones.modules.menu.fastinv.ItemBuilder;
import ru.mcsnapix.snapistones.modules.menu.inventory.PaginatedInventory;
import ru.mcsnapix.snapistones.utils.placeholder.PlaceholderUtil;
import ru.mcsnapix.snapistones.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Menu extends FastInv {
    private final Settings settings;
    private final SnapiStones plugin = SnapiStones.get();
    private final Player player;
    private final ProtectedRegion region;
    private final ProtectionBlock pb;

    public Menu(Settings settings, Player player, ProtectedRegion region, ProtectionBlock pb) {
        super(settings.getInt("Size"), settings.get("Title"));
        this.settings = settings;
        this.player = player;
        this.region = region;
        this.pb = pb;
        ConfigurationSection items = settings.getConfig().getConfigurationSection("Items");

        for (String item : items.getKeys(false)) {
            ConfigurationSection itemInfo = items.getConfigurationSection(item);

            String itemMaterial = itemInfo.getString("Material");
            int itemAmount = itemInfo.getInt("Amount");
            int itemSlot = itemInfo.getInt("Slot");
            String itemName = itemInfo.getString("Name");
            String itemHead = itemInfo.getString("Head");
            String itemPotion = itemInfo.getString("Potion");
            List<String> itemLore = PlaceholderUtil.getStringList(itemInfo.getStringList("Lore"), player, region, pb);

            if (itemHead != null) {
                ItemStack skull = HeadLib.setSkullOwner(
                        new ItemStack(Material.PLAYER_HEAD), UUID.randomUUID(), itemHead);
                setItem(itemSlot, new ItemBuilder(skull).amount(itemAmount).name(itemName).lore(itemLore).build());
                continue;
            }

            if (itemPotion != null) {
                Potion p = new Potion(PotionType.valueOf(itemPotion));
                ItemStack potion = p.toItemStack(1);
                PotionMeta meta = (PotionMeta) potion.getItemMeta();

                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                potion.setItemMeta(meta);
                setItem(itemSlot, new ItemBuilder(potion).amount(itemAmount).name(itemName).lore(itemLore).build());
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
        ConfigurationSection itemSection = settings.getConfig().getConfigurationSection("Items");
        MenuModule menuModule = plugin.getModuleManager().getMenu();

        for (String itemKey : itemSection.getKeys(false)) {
            ConfigurationSection itemInfo = itemSection.getConfigurationSection(itemKey);
            if (itemInfo.getInt("Slot") == event.getSlot()) {
                List<String> itemActions = itemInfo.getStringList("Actions");
                for (String action : itemActions) {
                    if (!action.startsWith("open: ")) {
                        continue;
                    }

                    String replacedAction = action.replace("open: ", "");

                    switch (replacedAction.toLowerCase()) {
                        case "upgrades/mainmenu":
                            openMenu(menuModule.getUpgradesSettings());
                            break;
                        case "mainmenu":
                            openMenu(menuModule.getMainSettings());
                            break;
                        case "page":
//                            List<ItemStack> items = new ArrayList<>();
//                            int count = 0;
//                            for (Material material : Material.values()) {
//                                count++;
//                                if (count == 50) {
//                                    break;
//                                }
//                                items.add(new ItemStack(material));
//                            }
//                            System.out.println(items.size() + " items added");
                            List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
                            PaginatedInventory paginatedInventory = new PaginatedInventory("Список игроков", list, 21);
                            paginatedInventory.openInventory(player);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void openMenu(Settings settings) {
        new Menu(settings, player, region, pb).open(player);
    }
}
