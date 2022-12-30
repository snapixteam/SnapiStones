package ru.mcsnapix.snapistones.modules.customflags.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.region.RegionCreateEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.customflags.utils.FlagUtil;
import ru.mcsnapix.snapistones.utils.Utils;

public class FlagListener implements Listener {
    private final Settings settings;

    public FlagListener(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onRegionCreate(RegionCreateEvent event) {
        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();
        String nameMaterial = Utils.getItemStackName(event.getProtectionBlock().getItem());

        FlagUtil.initFlag(region, player, settings.getList(nameMaterial + ".flags"));
    }
}
