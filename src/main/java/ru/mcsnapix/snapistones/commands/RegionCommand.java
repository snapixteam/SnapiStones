package ru.mcsnapix.snapistones.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.home.Home;
import ru.mcsnapix.snapistones.utils.ConfigUtil;
import ru.mcsnapix.snapistones.utils.placeholder.PlayerUtil;
import ru.mcsnapix.snapistones.utils.WGUtil;

@CommandAlias("rg|region")
public class RegionCommand extends BaseCommand {
    private final SnapiStones plugin = SnapiStones.get();

    @Subcommand("info")
    @CommandCompletion("@regionlist")
    public void onInfo(Player player, String[] args) {
        if (args.length == 0) {
            String id = plugin.getProtection().getRegionID(player.getLocation());
            if (id.equals("")) {
                ConfigUtil.sendMessage(player, "language.correctUseInfo");
                return;
            }
            ProtectedRegion region = plugin.getProtection().getRegion(player.getLocation());
            PlayerUtil.sendArrayMessage("language.info", player, region);
            return;
        }
        String id = args[0];

        if (!WGUtil.hasRegion(player, id)) {
            player.sendMessage(replaceRGID("language.noRegionWithName", id));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(player, id);
        PlayerUtil.sendArrayMessage("language.info", player, region);
    }

    @Subcommand("remove")
    @CommandCompletion("@myregionlistbyowner @players")
    public void onRemove(Player player, String[] args) {
        if (args.length == 0) {
            ConfigUtil.sendMessage(player, "language.correctUse.remove.1");
            return;
        }

        String id = args[0];

        if (!WGUtil.hasRegion(player, id)) {
            player.sendMessage(replaceRGID("language.correctUse.remove.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(player, id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.4", player, region);
            return;
        }

        String name = args[1];

        if (!Bukkit.getServer().getOfflinePlayer(name).hasPlayedBefore()) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.5", player, region);
            return;
        }

        Player p = Bukkit.getPlayerExact(name);
        if (!(region.getMembers().contains(p.getUniqueId()) || region.getOwners().contains(p.getUniqueId()))) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.6", player, region);
            return;
        }

        if (name.equalsIgnoreCase(player.getDisplayName())) {
            PlayerUtil.sendStringMessage("language.correctUse.remove.7", player, region);
            return;
        }

        region.getMembers().removePlayer(name);
        region.getOwners().removePlayer(name);
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

        if (!WGUtil.hasRegion(player, id)) {
            player.sendMessage(replaceRGID("language.correctUse.addMember.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(player, id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.4", player, region);
            return;
        }

        String name = args[1];
        if (!Bukkit.getServer().getOfflinePlayer(name).hasPlayedBefore()) {
            PlayerUtil.sendStringMessage("language.correctUse.addMember.5", player, region);
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

        region.getMembers().addPlayer(name);
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

        if (!WGUtil.hasRegion(player, id)) {
            player.sendMessage(replaceRGID("language.correctUse.addOwner.2", id));
            return;
        }
        ProtectedRegion region = WGUtil.getRegion(player, id);

        if (!region.getOwners().contains(player.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.3", player, region);
            return;
        }

        if (args.length == 1) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.4", player, region);
            return;
        }

        String name = args[1];
        if (!Bukkit.getServer().getOfflinePlayer(name).hasPlayedBefore()) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.5", player, region);
            return;
        }

        if (name.equalsIgnoreCase(player.getDisplayName())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.6", player, region);
            return;
        }

        Player p = Bukkit.getPlayerExact(name);
        if (!region.getOwners().contains(p.getUniqueId())) {
            PlayerUtil.sendStringMessage("language.correctUse.addOwner.7", player, region);
            return;
        }

        region.getMembers().removePlayer(name);
        region.getOwners().addPlayer(name);

        PlayerUtil.sendStringMessage("language.correctUse.addOwner.8", player, region);
    }

    @Subcommand("sethome")
    public void onSetHome(Player player, String[] args) {
        if (!plugin.getModuleManager().check("home")) {
            return;
        }

        Home home = plugin.getModuleManager().getHome();
        Settings settings = home.getSettings();

        String id = plugin.getProtection().getRegionID(player.getLocation());
        if (id.equals("")) {
            player.sendMessage(settings.get("SetHome.NotInRegion"));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(player, id);
        if (!region.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(settings.get("SetHome.NotOwner"));
            return;
        }

        home.addHome(id, player.getLocation());
        player.sendMessage(settings.get("SetHome.Success"));
    }

    @Subcommand("home")
    @CommandCompletion("@myregionlistbymember")
    public void onHome(Player player, String[] args) {
        if (!plugin.getModuleManager().check("home")) {
            return;
        }

        Home home = plugin.getModuleManager().getHome();
        Settings settings = home.getSettings();

        if (args.length == 0) {
            player.sendMessage(settings.get("Home.RegionWrite"));
            return;
        }

        String id = args[0];
        if (!WGUtil.hasRegion(player, id)) {
            player.sendMessage(settings.get("Home.NoRegion"));
            return;
        }

        ProtectedRegion region = WGUtil.getRegion(player, id);
        if (!(region.getOwners().contains(player.getUniqueId()) || region.getMembers().contains(player.getUniqueId()))) {
            player.sendMessage(settings.get("Home.NotMember"));
            return;
        }

        player.teleport(home.getHomeLoc(id));
        player.sendMessage(settings.get("Home.Success"));
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfig().getStringList("language.help").toArray(new String[0]));
    }

    private String replaceRGID(String path, String id) {
        return ConfigUtil.getString(path).replace("%region_id%", id);
    }
}
