package ru.mcsnapix.snapistones.modules.holograms.listener;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.region.RegionCreateEvent;
import ru.mcsnapix.snapistones.api.events.region.RegionRemoveEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.utils.Utils;

public class HologramListener implements Listener {
    private final Settings settings;

    public HologramListener(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onRegionCreate(RegionCreateEvent event) {
        String id = event.getRegion().getId();
        String nameMaterial = Utils.getItemStackName(event.getProtectionBlock().getItem());
        DHAPI.createHologram(id, event.getLocation().add(0.5, settings.getDouble(nameMaterial + ".height"), 0.5), settings.getList(nameMaterial + ".lines"));
    }

    @EventHandler
    public void onRegionRemove(RegionRemoveEvent event) {
        String id = event.getRegion().getId();
        DHAPI.removeHologram(id);
    }
}
