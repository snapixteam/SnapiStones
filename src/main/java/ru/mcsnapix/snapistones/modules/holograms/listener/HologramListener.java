package ru.mcsnapix.snapistones.modules.holograms.listener;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.region.RegionCreateEvent;
import ru.mcsnapix.snapistones.api.events.region.RegionRemoveEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.utils.Utils;
import ru.mcsnapix.snapistones.utils.placeholder.PlaceholderUtil;

public class HologramListener implements Listener {
    private final Settings settings;

    public HologramListener(Settings settings) {
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionCreate(RegionCreateEvent event) {
        String id = event.getRegion().getId();
        String nameMaterial = Utils.getItemStackName(event.getProtectionBlock().getItem());
        Hologram hologram = DHAPI.createHologram(id, event.getLocation().add(0.5, settings.getDouble(nameMaterial + ".height"), 0.5), PlaceholderUtil.getStringList(settings.getList(nameMaterial + ".lines"), event.getPlayer(), event.getRegion(), event.getProtectionBlock()));
        hologram.save();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionRemove(RegionRemoveEvent event) {
        String id = event.getRegion().getId();
        DHAPI.removeHologram(id);
    }
}
