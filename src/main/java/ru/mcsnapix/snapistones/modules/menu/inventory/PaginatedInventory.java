package ru.mcsnapix.snapistones.modules.menu.inventory;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.modules.menu.fastinv.ItemBuilder;
import ru.mcsnapix.snapistones.utils.placeholder.PlaceholderUtil;
import ru.mcsnapix.snapistones.utils.placeholder.PlayerUtil;

import java.util.List;

@Getter
public class PaginatedInventory implements InventoryHolder {
    private final List<Player> allPlayers;
    private final int maxItemsPerPage;
    private final Inventory inventory;
    private int currentPage;
    private int count;

    public PaginatedInventory(String title, List<Player> allPlayers, int maxItemsPerPage) {
        this.allPlayers = allPlayers;
        this.maxItemsPerPage = maxItemsPerPage;
        this.currentPage = 0;
        this.count = 0;
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public void openInventory(Player player) {
        updateInventory(false, false);
        player.openInventory(inventory);
    }

    public void updateInventory(boolean prev, boolean next) {
        inventory.clear();

        int rows = (int) Math.ceil((double) maxItemsPerPage / 7.0)+1;
        int columns = 9;

        if (prev) {
            currentPage--;
        } else if (next) {
            currentPage++;
        }

        if (currentPage == 0) {
            count = 0;
        } else {
            count = maxItemsPerPage;
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Skip the first row and columns 1 and 9 of all other rows
                if (row == 0 || col == 0 || col == 8) {
                    continue;
                }

                if (allPlayers.size() == count) {
                    break;
                }

                Player player = allPlayers.get(count);

                ConfigurationSection listPlayers = SnapiStones.get().getConfig().getConfigurationSection("language.listPlayers");
                String name = PlaceholderUtil.fillCommonPlaceholders(listPlayers.getString("name"), player, null, null);
                List<String> lore = listPlayers.getStringList("lore");

                ItemStack head = PlayerUtil.getPlayerSkull(player);
                inventory.setItem(row * 9 + col, new ItemBuilder(head).amount(1).name(name).lore(lore).build());
                count++;
            }
        }

        // Add pagination buttons
        ItemStack prevButton = new ItemStack(Material.ARROW, 1);
        ItemMeta prevButtonMeta = prevButton.getItemMeta();
        prevButtonMeta.setDisplayName(ChatColor.RED + "Previous Page");
        prevButton.setItemMeta(prevButtonMeta);
        if (currentPage > 0) {
            inventory.setItem(45, prevButton);
        }

        ItemStack nextButton = new ItemStack(Material.ARROW, 1);
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        nextButtonMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        nextButton.setItemMeta(nextButtonMeta);
        if (Math.min(count + maxItemsPerPage, allPlayers.size()) < allPlayers.size()) {
            inventory.setItem(53, nextButton);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

