package ru.mcsnapix.snapistones.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.api.ProtectionBlock;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.enums.Column;
import ru.mcsnapix.snapistones.modules.home.HomeModule;
import ru.mcsnapix.snapistones.modules.menu.MenuModule;
import ru.mcsnapix.snapistones.modules.menu.menus.Menu;
import ru.mcsnapix.snapistones.modules.upgrade.managers.UpgradeDatabaseManager;
import ru.mcsnapix.snapistones.mysql.RegionDatabaseManager;
import ru.mcsnapix.snapistones.utils.ConfigUtil;
import ru.mcsnapix.snapistones.utils.Utils;
import ru.mcsnapix.snapistones.utils.placeholder.PlayerUtil;
import ru.mcsnapix.snapistones.utils.WGUtil;
import ru.mcsnapix.snapistones.utils.serializer.LocationSerializer;
import ru.mcsnapix.snapistones.xseries.XMaterial;

import java.util.Iterator;
import java.util.Map;

@CommandAlias("rg|region")
public class RegionCommand extends BaseCommand {
    private final SnapiStones plugin = SnapiStones.get();

    @Subcommand("info")
    @CommandCompletion("@regionlist")
    public void onInfo(Player player, String[] args) {
        if (args.length == 0) {
            String id = plugin.getProtection().getRegionID(player.getLocation());
            if (id.isEmpty() || id.equalsIgnoreCase("lobby")) {
                ConfigUtil.sendMessage(player, "language.correctUseInfo");
                return;
            }
            ProtectedRegion region = plugin.getProtection().getRegion(player.getLocation());
            PlayerUtil.sendArrayMessage("language.info", player, region);
            return;
        }
        String id = args[0];

        if (!WGUtil.hasRegion(id)) {
            player.sendMessage(replaceRGID("language.noRegionWithName", id));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(id);
        Block block = Bukkit.getWorld("world").getBlockAt(WGUtil.getCenter(region));
        XMaterial item = XMaterial.matchXMaterial(block.getType());
        ProtectionBlock pb = plugin.getProtection().getProtectionBlock(item);

        PlayerUtil.sendArrayMessage("language.info", player, region, pb);
    }

    @Subcommand("list")
    @CommandCompletion("@myregionlistbymember")
    public void onList(Player player) {
        Audience p = plugin.adventure().player(player);
        StringBuilder s = new StringBuilder();
        String s1 = "<newline>  <aqua><b>Список регионов</b></aqua><newline>       ";
        Component header = MiniMessage.miniMessage().deserialize(s1);
        p.sendMessage(header);
        Iterator<ProtectedRegion> iterator = WGUtil.getRegions(player).iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            ProtectedRegion region = iterator.next();
            Location center = WGUtil.getCenter(region.getMinimumPoint(), region.getMaximumPoint());
            s.append("  <white>").append(count).append(". <green><hover:show_text:'<green>Нажмите, чтобы открыть меню региона'><click:run_command:/rg menu ").append(region.getId()).append(">").append(region.getId()).append("</click></hover> <gray>– <white>").append(LocationSerializer.getFormattedLocation(center));
            if (iterator.hasNext()) {
                s.append("<newline>");
            }
        }
        if (count == 0) {
            s.append("  <white>У вас <red>нет <white>регионов");
        }
        s.append("<newline>");
        Component parsed = MiniMessage.miniMessage().deserialize(s.toString());
        p.sendMessage(parsed);
    }

    @Subcommand("remove")
    @CommandCompletion("@myregionlistbyowner @players")
    public void onRemove(Player player, String[] args) {
        if (args.length == 0) {
            ConfigUtil.sendMessage(player, "language.correctUse.remove.1");
            return;
        }

        String id = args[0];

        if (!WGUtil.hasRegion(id)) {
            player.sendMessage(replaceRGID("language.correctUse.remove.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.4", player, region);
            return;
        }

        String name = args[1];

        if (!(RegionDatabaseManager.hasMembers(region.getId(), Column.MEMBERS) || RegionDatabaseManager.hasMembers(region.getId(), Column.OWNERS))) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.6", player, region);
            return;
        }

        if (name.equalsIgnoreCase(player.getName())) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.7", player, region);
            return;
        }

        if (RegionDatabaseManager.hasMembers(region.getId(), Column.MEMBERS)) {
            RegionDatabaseManager.removeMembers(region.getId(), name, Column.MEMBERS);
            region.getMembers().removePlayer(name);
        }

        if (RegionDatabaseManager.hasMembers(region.getId(), Column.OWNERS)) {
            RegionDatabaseManager.removeMembers(region.getId(), name, Column.OWNERS);
            region.getOwners().removePlayer(name);
        }

        PlayerUtil.sendStringMessage("language.correctUse.remove.8", player, region);
    }

    @Subcommand("addmember")
    @CommandCompletion("@myregionlistbyowner @players")
    public void onAddMember(Player player, String[] args) {
        if (args.length == 0) {
            ConfigUtil.sendMessage(player, "language.correctUse.addMember.1");
            return;
        }

        String id = args[0];

        if (!WGUtil.hasRegion(id)) {
            player.sendMessage(replaceRGID("language.correctUse.addMember.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.4", player, region);
            return;
        }

        String name = args[1];
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.5", player, region);
            return;
        }

        if (!p.isOnline()) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.5", player, region);
            return;
        }

        if (name.equalsIgnoreCase(player.getDisplayName())) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.6", player, region);
            return;
        }

        if (region.getMembers().contains(name) || region.getOwners().contains(name)) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.7", player, region);
            return;
        }

        if (RegionDatabaseManager.getMembers(region.getId(), Column.MEMBERS) != null) {
            if (UpgradeDatabaseManager.getMaxMembers(region.getId(), Column.MAX_MEMBERS) <= RegionDatabaseManager.getMembers(region.getId(), Column.MEMBERS).size()) {
                PlayerUtil.sendDStringMessage(plugin.getModuleManager().getUpgrade().getSettings().get("Language.Limit.AddMember"), player, region);
                return;
            }
        }

        region.getMembers().addPlayer(name);
        RegionDatabaseManager.addMembers(region.getId(), player.getName(), Column.MEMBERS);
        PlayerUtil.sendStringMessage("language.correctUse.addMember.8", player, region);
    }

    @Subcommand("addowner")
    @CommandCompletion("@myregionlistbyowner @players")
    public void onAddOwner(Player player, String[] args) {
        if (args.length == 0) {
            ConfigUtil.sendMessage(player, "language.correctUse.addOwner.1");
            return;
        }

        String id = args[0];

        if (!WGUtil.hasRegion(id)) {
            player.sendMessage(replaceRGID("language.correctUse.addOwner.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.4", player, region);
            return;
        }

        String name = args[1];
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.5", player, region);
            return;
        }

        if (!p.isOnline()) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.5", player, region);
            return;
        }

        if (name.equalsIgnoreCase(player.getName())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.6", player, region);
            return;
        }

        if (region.getOwners().contains(p.getName())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.7", player, region);
            return;
        }

        if (RegionDatabaseManager.getMembers(region.getId(), Column.OWNERS) != null) {
            if (UpgradeDatabaseManager.getMaxMembers(region.getId(), Column.MAX_OWNERS) <= RegionDatabaseManager.getMembers(region.getId(), Column.OWNERS).size()) {
                PlayerUtil.sendDStringMessage(plugin.getModuleManager().getUpgrade().getSettings().get("Language.Limit.AddOwner"), player, region);
                return;
            }
        }

        if (region.getMembers().contains(player.getName())) {
            region.getMembers().removePlayer(name);
            RegionDatabaseManager.removeMembers(region.getId(), name, Column.MEMBERS);
        }

        region.getOwners().addPlayer(name);
        RegionDatabaseManager.addMembers(region.getId(), name, Column.OWNERS);

        PlayerUtil.sendStringMessage("language.correctUse.addOwner.8", player, region);
    }

    @Subcommand("sethome")
    public void onSetHome(Player player, String[] ignoredArgs) {
        if (!plugin.getModuleManager().check("home")) {
            return;
        }

        HomeModule home = plugin.getModuleManager().getHome();
        Settings settings = home.getSettings();

        String id = plugin.getProtection().getRegionID(player.getLocation());
        if (id.isEmpty()) {
            player.sendMessage(settings.get("SetHome.NotInRegion"));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(id);
        if (!region.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(settings.get("SetHome.NotOwner"));
            return;
        }

        home.addHome(id, player.getLocation());
        player.sendMessage(settings.get("SetHome.Success"));
    }

    @Subcommand("home")
    @CommandCompletion("@myregionhomelistbymember")
    public void onHome(Player player, String[] args) {
        if (!plugin.getModuleManager().check("home")) {
            return;
        }

        HomeModule home = plugin.getModuleManager().getHome();
        Settings settings = home.getSettings();

        int count = 0;
        ProtectedRegion region = null;

        for (Map.Entry<String, ProtectedRegion> entry : plugin.getRegionManager().getRegions().entrySet()) {
            ProtectedRegion pr = entry.getValue();
            if (WGUtil.hasPlayerInRegion(pr, player)) {
                if (home.hasLocationHome(pr.getId())) {
                    count++;
                    region = pr;
                }
            }
        }

        if (count == 1) {
            home.teleportHome(player, region);
            return;
        }

        if (args.length == 0) {
            PlayerUtil.sendDStringMessage(settings.get("Home.RegionWrite"), player, region);
            return;
        }

        String id = args[0];
        if (!WGUtil.hasRegion(id)) {
            PlayerUtil.sendDStringMessage(settings.get("Home.NoRegion"), player, region);
            return;
        }

        region = WGUtil.getRegion(id);
        if (!WGUtil.hasPlayerInRegion(region, player)) {
            PlayerUtil.sendDStringMessage(settings.get("Home.NotMember"), player, region);
            return;
        }

        home.teleportHome(player, region);
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfig().getStringList("language.help").toArray(new String[0]));
    }

    @Subcommand("menu")
    @CommandCompletion("@myregionlistbymember")
    public void onMenu(Player player, String[] args) {
        if (!plugin.getModuleManager().check("menu")) {
            return;
        }

        MenuModule menu = plugin.getModuleManager().getMenu();
        Settings settings = menu.getMainSettings();


        if (args.length == 0) {
            String id = plugin.getProtection().getRegionID(player.getLocation());
            if (id.isEmpty()) {
                player.sendMessage(settings.get("Command.CorrectUse"));
                return;
            }
            ProtectedRegion region = plugin.getProtection().getRegion(player.getLocation());

            if (!WGUtil.hasPlayerInRegion(region, player)) {
                player.sendMessage(settings.get("Command.NoMember"));
                return;
            }

            Block block = Bukkit.getWorld("world").getBlockAt(WGUtil.getCenter(region));
            XMaterial item = XMaterial.matchXMaterial(block.getType());
            ProtectionBlock pb = plugin.getProtection().getProtectionBlock(item);

            new Menu(settings, player, region, pb).open(player);
            return;
        }
        String id = args[0];

        if (!WGUtil.hasRegion(id)) {
            player.sendMessage(replaceRGID("Command.noRegionWithName", id));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(id);
        if (!WGUtil.hasPlayerInRegion(region, player)) {
            player.sendMessage(settings.get("Command.NoMember"));
            return;
        }

        Block block = Bukkit.getWorld("world").getBlockAt(WGUtil.getCenter(region));
        XMaterial item = XMaterial.matchXMaterial(block.getType());
        ProtectionBlock pb = plugin.getProtection().getProtectionBlock(item);

        new Menu(settings, player, region, pb).open(player);
    }

    private String replaceRGID(String path, String id) {
        return ConfigUtil.getString(path).replace("%region_id%", id);
    }
}
